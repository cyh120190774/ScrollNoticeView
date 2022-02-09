# ScrollNoticeView[![](https://jitpack.io/v/cyh120190774/ScrollNoticeView.svg)](https://jitpack.io/#cyh120190774/ScrollNoticeView)

滚动播放的公告控件  

![](./images/lmivh-ew81t_1.gif)

## Gradle

``` groovy
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```
``` groovy
	dependencies {
	        implementation 'com.github.cyh120190774:ScrollNoticeView:1.00'
	}
```
## Usage

**XML**

``` xml 
        <com.cyh.scrollnoticeview.ScrollNoticeView
            android:id="@+id/tv_notice1"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginStart="8dp"
            android:layout_marginEnd="10dp"
            app:nsvInterval="3000"
            app:nsvTextColor="#4071FF"
            app:nsvTextSize="12sp" />
```

**Java**

``` java
public static String[] notices = new String[]{
            "春节（Spring Festival），即中国农历新年.",
            "春节历史悠久，由上古时代岁首祈岁祭祀演变而来。万物本乎天、人本乎祖，祈岁祭祀、敬天法祖，报本反始也。",
            "春节的起源蕴含着深邃的文化内涵，在传承发展中承载了丰厚的历史文化底蕴。在春节期间，全国各地均有举行各种庆贺新春活动，带有浓郁的各地域特色。",
    };

  binding.tvNotice1.start(Arrays.asList(notices));
        binding.tvNotice1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, notices[binding.tvNotice1.getIndex()], Toast.LENGTH_SHORT).show();
            }
        });
```





**属性**

``` xml  
  <declare-styleable name="ScrollNoticeView">
        <!-- 图标 -->
        <attr name="nsvIcon" format="reference" />
        <!-- 图标与内容的间隙 -->
        <attr name="nsvIconPadding" format="dimension" />
        <!-- 图标颜色 -->
        <attr name="nsvIconTint" format="color" />
        <!-- 文本尺寸 -->
        <attr name="nsvTextSize" format="dimension" />
        <!-- 文本颜色 -->
        <attr name="nsvTextColor" format="color" />
        <!-- 文本最大行数 -->
        <attr name="nsvTextMaxLines" format="integer" />
        <!-- 切换动画间隔时间，毫秒 -->
        <attr name="nsvInterval" format="integer" />
        <!-- 切换动画持续时间，毫秒 -->
        <attr name="nsvDuration" format="integer" />
        <!-- 文字滚动速度 负数为向右-->
        <attr name="nsvSpeed" format="integer" />
        <!-- 文字开始滚动的延时-->
        <attr name="nsvDelay" format="integer" />
    </declare-styleable>

```