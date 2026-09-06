# Android Library 发布到 Maven Central 备忘

本仓库的 `dev_toolkit` / `dev_adkit` **只发布到本地 Maven**（`E:/MavenRepo`），不再发远端。  
本文档整理自本项目曾用过的 Central 发布配置，供以后开发**可公开**的库时直接复用。

官方参考：[Central Portal / OSSRH Staging API](https://central.sonatype.org/publish/publish-portal-ossrh-staging-api/)

---

## 1. 前置条件

1. 在 [Central Portal](https://central.sonatype.com/) 注册账号，并验证命名空间（如 GitHub 用户对应的 `io.github.<username>`）。
2. 准备 GPG 签名密钥（Central 强制要求签名）。
3. 在本机 `~/.gradle/gradle.properties`（Windows：`C:\Users\<你>\.gradle\gradle.properties`）配置凭证与密钥，**不要提交到 Git**：

```properties
mavenCentralUsername=<Central Portal 用户名或 token 用户名>
mavenCentralPassword=<Central Portal 密码或 token>

# 方式 A：密钥文件（推荐）
signingKeyFile=C:/Users/<你>/.gnupg/secring-or-exported.asc
signingInMemoryKeyPassword=<GPG 私钥口令>
# 可选：指定 keyId
# signingInMemoryKeyId=<8 或 16 位 key id>

# 方式 B：内存密钥（把 armored 私钥写成单行，换行写成 \n）
# signingInMemoryKey=-----BEGIN PGP PRIVATE KEY BLOCK-----\n...\n-----END PGP PRIVATE KEY BLOCK-----
# signingInMemoryKeyPassword=<GPG 私钥口令>
```

---

## 2. 模块 `build.gradle` 模板

把下面整段接到 Android Library 模块（按需改 `PUBLISH_*` 与 POM 信息）。

```gradle
plugins {
    id 'com.android.library'
    id 'kotlin-android'
    id 'maven-publish'
    id 'signing'
}

ext {
    PUBLISH_GROUP_ID = 'io.github.yourname'
    PUBLISH_ARTIFACT_ID = 'your-lib'
    PUBLISH_VERSION = '1.0.0'
}

android {
    // ... 你的 android {} 配置 ...

    publishing {
        singleVariant("release") {
            withSourcesJar()
            withJavadocJar()
        }
    }
}

afterEvaluate {
    publishing {
        publications {
            release(MavenPublication) {
                from components.release
                groupId = PUBLISH_GROUP_ID
                artifactId = PUBLISH_ARTIFACT_ID
                version = PUBLISH_VERSION

                // Central 要求 POM 含 license / developer / scm 等元数据
                pom {
                    name = 'Your Lib'
                    description = '公开库简介'
                    url = 'https://github.com/yourname/YourRepo'
                    inceptionYear = '2025'

                    licenses {
                        license {
                            name = 'The Apache License, Version 2.0'
                            url = 'https://www.apache.org/licenses/LICENSE-2.0.txt'
                            distribution = 'repo'
                        }
                    }

                    developers {
                        developer {
                            id = 'yourname'
                            name = 'yourname'
                            email = 'you@example.com'
                            url = 'https://github.com/yourname'
                        }
                    }

                    scm {
                        connection = 'scm:git:git://github.com/yourname/YourRepo.git'
                        developerConnection = 'scm:git:ssh://github.com/yourname/YourRepo.git'
                        url = 'https://github.com/yourname/YourRepo'
                    }
                }
            }
        }

        repositories {
            maven {
                name = 'MavenCentral'
                url = uri('https://ossrh-staging-api.central.sonatype.com/service/local/staging/deploy/maven2/')
                credentials {
                    username = findProperty('mavenCentralUsername') ?: ''
                    password = findProperty('mavenCentralPassword') ?: ''
                }
            }
        }
    }

    signing {
        // Prefer armored key file; fallback to signingInMemoryKey (single-line with \n)
        def keyFilePath = findProperty('signingKeyFile')?.toString()
        def inMemoryKey = findProperty('signingInMemoryKey')?.toString()
        def keyId = findProperty('signingInMemoryKeyId')?.toString()
        def keyPassword = findProperty('signingInMemoryKeyPassword')?.toString() ?: ''

        def key = null
        if (keyFilePath) {
            def keyFile = file(keyFilePath)
            if (!keyFile.exists()) {
                throw new GradleException("signingKeyFile not found: ${keyFilePath}")
            }
            key = keyFile.getText('UTF-8')
        } else if (inMemoryKey) {
            key = inMemoryKey.replace('\\n', '\n')
        }

        if (key) {
            if (keyId) {
                useInMemoryPgpKeys(keyId, key, keyPassword)
            } else {
                useInMemoryPgpKeys(key, keyPassword)
            }
        }

        required { true }
        sign publishing.publications.release
    }
}

// 上传到 staging 后通知 Central Portal，部署才会出现在控制台
// See: https://central.sonatype.org/publish/publish-portal-ossrh-staging-api/
tasks.register('notifyCentralPortal') {
    group = 'publishing'
    description = 'Notify Central Portal after publishing so the deployment becomes visible'

    doLast {
        def username = findProperty('mavenCentralUsername')
        def password = findProperty('mavenCentralPassword')
        if (!username || !password) {
            throw new GradleException('Missing mavenCentralUsername / mavenCentralPassword in ~/.gradle/gradle.properties')
        }

        def auth = "${username}:${password}".bytes.encodeBase64().toString()
        def url = new URL("https://ossrh-staging-api.central.sonatype.com/manual/upload/defaultRepository/${PUBLISH_GROUP_ID}")
        def conn = url.openConnection()
        conn.requestMethod = 'POST'
        conn.setRequestProperty('Authorization', "Basic ${auth}")
        conn.doOutput = true
        conn.outputStream.close()

        def code = conn.responseCode
        if (code < 200 || code >= 300) {
            def errorBody = ''
            try {
                errorBody = conn.errorStream?.text
            } catch (ignored) {
            }
            throw new GradleException("Failed to notify Central Portal: HTTP ${code} ${errorBody}")
        }
        println "Central Portal notified for ${PUBLISH_GROUP_ID}. Open https://central.sonatype.com/publishing/deployments and click Publish."
    }
}

tasks.register('publishToMavenCentral') {
    group = 'publishing'
    description = 'Publish release to Maven Central staging and notify Central Portal'
    dependsOn 'publishReleasePublicationToMavenCentralRepository', 'notifyCentralPortal'
    tasks.findByName('notifyCentralPortal')?.mustRunAfter('publishReleasePublicationToMavenCentralRepository')
}
```

---

## 3. 发布命令与上线步骤

```bash
# 在库模块所在工程根目录执行
./gradlew :your_module:publishToMavenCentral
```

1. 命令成功后，打开 [Central Portal Deployments](https://central.sonatype.com/publishing/deployments)。
2. 找到刚通知上来的 deployment，检查校验结果。
3. 点击 **Publish**，等待同步到 Maven Central（通常数十分钟到数小时）。
4. 消费方依赖：

```gradle
implementation 'io.github.yourname:your-lib:1.0.0'
```

---

## 4. 常见注意点

| 项 | 说明 |
|----|------|
| 命名空间 | `groupId` 必须与 Portal 已验证的 namespace 一致 |
| 版本 | 已成功发布的版本一般不可覆盖，需升版本号 |
| 签名 | 无私钥或 `required { true }` 时发布会失败 |
| sources / javadoc | `withSourcesJar()` / `withJavadocJar()` 建议保留，符合 Central 习惯 |
| 凭证 | 只放在本机 `gradle.properties`，勿提交仓库 |
| 密钥泄露 | 若私钥泄露，应立刻 revoke 并换新 key |

---

## 5. 本仓库当前本地发布（对照）

本仓库私有封装库仅用本地仓，消费方 `settings.gradle` 已配置：

```gradle
maven {
    url = uri("E:/MavenRepo")
}
```

发布命令：

```powershell
.\gradlew :dev_toolkit:publishToLocalMaven
.\gradlew :dev_adkit:publishToLocalMaven
```

依赖坐标示例：

```gradle
implementation 'io.github.cooldev116:dev-toolkit:1.0.3'
implementation 'io.github.cooldev116:dev-adkit:1.0.5'
```
