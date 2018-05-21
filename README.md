# FollowFinger
这是一个自定义view，可嵌套小空件实现控件跟随手指移动（有边界）

Step 1. Add it in your root build.gradle at the end of repositories:
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
Step 2. Add the dependency
	dependencies {
	        implementation 'com.github.whiteQin:FollowFinger:1.0.0'
	}

使用方法为
     <com.followfingerlibrary.FingerFollowView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content">

            <TextView
                android:layout_width="50dp"
                android:layout_height="20dp"
                android:text="finger"
                android:textSize="20sp"
                android:gravity="center"
                android:textColor="#ffffff"
                android:background="#000000"/>

     </com.followfingerlibrary.FingerFollowView>

布局类似Lineralyout，竖直排列，后期加入横向排列

感谢 huachao1001 博客地址：https://blog.csdn.net/huachao1001