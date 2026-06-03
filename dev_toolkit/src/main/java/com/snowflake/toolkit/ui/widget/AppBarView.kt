package com.snowflake.toolkit.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.snowflake.toolkit.R
import com.snowflake.toolkit.databinding.ToolkitViewAppBarBinding
import com.snowflake.toolkit.ext.click
import com.snowflake.toolkit.ext.sp2Px
import com.snowflake.toolkit.ext.txt

/**
 * @description:标题栏
 * @author:Melon
 * @date:2025/6/4
 */
class AppBarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) :
    ConstraintLayout(context, attrs, defStyle) {

    private lateinit var mBinding: ToolkitViewAppBarBinding

    private var mBackAction: (() -> Unit)? = null

    /**
     * 右侧传入自定义布局，并且需要点击事件的id要带有改后缀
     */
    private val mCustomClickSuffix = "_click"

    private var mOnRightCustomClickAction: ((view: View) -> Unit)? = null

    /**
     * 右侧文字或者图标点击监听
     */
    private var mRightClickAction: (() -> Unit)? = null

    companion object {

        /**
         * 返回图标资源id
         */
        private var mGlobalBackIcon: Int? = null

        /**
         * @description 全局的设置的返回图标
         * @param iconResId 图标资源id
         * @author Melon
         * @time 2025/6/5 11:18
         */
        fun setGlobalBackIcon(@DrawableRes iconResId: Int) {
            mGlobalBackIcon = iconResId
        }
    }

    init {
        initView()
        initAttr(attrs)
        initListener()
    }

    /**
     * @description 初始化View
     * @author Melon
     * @time 2025/6/4 18:16
     */
    private fun initView() {
        mBinding = ToolkitViewAppBarBinding.bind(
            LayoutInflater.from(context).inflate(R.layout.toolkit_view_app_bar, this, false)
        )
        addView(mBinding.root)
    }

    /**
     * @description 初始化自定义属性
     * @author Melon
     * @time 2025/6/4 18:17
     */
    private fun initAttr(attrs: AttributeSet?) {
        attrs?.apply {
            val ta = context.obtainStyledAttributes(this, R.styleable.AppBarView)
            //标题
            val title = ta.getString(R.styleable.AppBarView_app_bar_title)
            mBinding.appBarTitle.txt(title)
            //标题颜色
            val titleColor =
                ta.getColor(
                    R.styleable.AppBarView_app_bar_title_color,
                    ContextCompat.getColor(context, android.R.color.black)
                )
            mBinding.appBarTitle.setTextColor(titleColor)
            //标题大小
            val titleSize =
                ta.getDimension(R.styleable.AppBarView_app_bar_title_size, 18f.sp2Px().toFloat())
            mBinding.appBarTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, titleSize)
            //是否包含状态栏，默认包含
            val isStatusBar = ta.getBoolean(R.styleable.AppBarView_app_bar_is_status_bar, true)
            mBinding.appBarStatusBar.isVisible = isStatusBar
            //返回图标
            val backIcon = ta.getResourceId(R.styleable.AppBarView_app_bar_back_icon, -1)
            val backRes = if (backIcon != -1) {
                backIcon
            } else {
                if (mGlobalBackIcon != null) {
                    mGlobalBackIcon!!
                } else {
                    R.drawable.toolkit_ic_black_back
                }
            }
            mBinding.appBarBack.setImageResource(backRes)
            val rightText = ta.getString(R.styleable.AppBarView_app_bar_right_text)
            if (!rightText.isNullOrEmpty()) {
                mBinding.appBarRightText.isVisible = true
                mBinding.appBarRightText.text = rightText
                val rightTextColor = ta.getColor(
                    R.styleable.AppBarView_app_bar_right_text_color,
                    ContextCompat.getColor(context, android.R.color.black)
                )
                mBinding.appBarRightText.setTextColor(rightTextColor)
                val rightTextSize = ta.getDimension(
                    R.styleable.AppBarView_app_bar_right_text_size,
                    14f.sp2Px().toFloat()
                )
                mBinding.appBarRightText.setTextSize(TypedValue.COMPLEX_UNIT_PX, rightTextSize)
            }
            //右侧图标
            val rightIcon = ta.getResourceId(R.styleable.AppBarView_app_bar_right_icon, -1)
            if (rightIcon != -1) {
                mBinding.appBarRightIcon.run {
                    isVisible = true
                    setImageResource(rightIcon)
                }
            }
            //右侧自定义布局
            val rightCustomLayoutId =
                ta.getResourceId(R.styleable.AppBarView_app_bar_right_custom_layout, -1)
            if (rightCustomLayoutId != -1) {
                val customView =
                    LayoutInflater.from(context)
                        .inflate(rightCustomLayoutId, this@AppBarView, false)
                mBinding.appBarLayout.run {
                    isVisible = true
                    addView(customView)
                }
                traverseAndAttach(customView)
            }
            ta.recycle()
        }
    }

    /**
     * @description 初始化点击事件
     * @author Melon
     * @time 2025/6/4 18:17
     */
    private fun initListener() {
        mBinding.appBarBack.click {
            mBackAction?.invoke()
        }

        mBinding.appBarRightText.click {
            mRightClickAction?.invoke()
        }
        mBinding.appBarRightIcon.click {
            mRightClickAction?.invoke()
        }
    }

    fun setOnNavigationListener(action: () -> Unit) {
        mBackAction = action
    }

    /**
     * @description 遍历所有的字View
     * @param view 右侧自定义的布局
     * @author Melon
     * @time 2025/6/5 14:30
     */
    private fun traverseAndAttach(view: View) {
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                setOnClickListener(view.getChildAt(i))
            }
        } else {
            setOnClickListener(view)
        }
    }

    /**
     * @description 设置点击事件
     * @param view 右侧自定义的布局遍历出来的子view
     * @author Melon
     * @time 2025/6/5 14:33
     */
    private fun setOnClickListener(view: View) {
        if (view.id != View.NO_ID && view.resources.getResourceEntryName(view.id)
                .endsWith(mCustomClickSuffix)
        ) {
            view.click {
                mOnRightCustomClickAction?.invoke(view)
            }
        }
    }

    fun setOnRightCustomLayoutListener(action: (view: View) -> Unit) {
        mOnRightCustomClickAction = action
    }

    /**
     * @description 设置标题
     * @param title 标题
     * @author Melon
     * @time 2025/6/5 14:44
     */
    fun setTitle(title: String?) {
        mBinding.appBarTitle.txt(title)
    }

    /**
     * @description 设置标题颜色
     * @param color 文字颜色
     * @author Melon
     * @time 2025/6/6 10:44
     */
    fun setTitleColor(@ColorInt color: Int) {
        mBinding.appBarTitle.setTextColor(color)
    }

    /**
     * @description 设置标题大小
     * @param titleSize 文字大小
     * @author Melon
     * @time 2025/6/6 10:47
     */
    fun setTitleSize(titleSize: Float) {
        mBinding.appBarTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, titleSize)
    }

    /**
     * @description 设置右侧文字
     * @param
     * @return
     * @author Melon
     * @time 2025/6/5 14:46
     */
    fun setRightText(text: String?) {
        mBinding.appBarRightText.run {
            isVisible = true
            txt(text)
        }
    }

    /**
     * @description 设置右侧文字颜色
     * @param color 颜色
     * @author Melon
     * @time 2025/6/6 10:52
     */
    fun setRightTextColor(@ColorInt color: Int) {
        mBinding.appBarRightText.setTextColor(color)
    }

    /**
     * @description 设置右侧文字大小
     * @param textSize 文字大小
     * @author Melon
     * @time 2025/6/6 10:53
     */
    fun setRightTextSize(textSize: Float) {
        mBinding.appBarRightText.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize)
    }

    /**
     * @description 设置右侧图标
     * @param icon 右侧图标
     * @author Melon
     * @time 2025/6/5 14:48
     */
    fun setRightIcon(@DrawableRes icon: Int) {
        mBinding.appBarRightIcon.run {
            isVisible = true
            setImageResource(icon)
        }
    }

    /**
     * @description 右侧文字或者图标点击监听
     * @author Melon
     * @time 2025/7/3 14:19
     */
    fun setRightClickListener(action: () -> Unit) {
        mRightClickAction = action
    }
}