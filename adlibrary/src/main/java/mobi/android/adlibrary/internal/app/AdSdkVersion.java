package mobi.android.adlibrary.internal.app;

/**
 * Created by Vincent on 2016/5/18.
 */
public class AdSdkVersion {
    //FIXME  没有限制用户传了父容器之后有可以调用getAdView的方法，这样可能造成广告的view多次注册导致注册被注销，
    //FIXME 需要修改。（影响FB,vk,flurry）
    //TODO maven:后边对应的是svn的版本号。
    //TODO SVN:http://192.168.5.250:8080/svn/mopub_ad_sdk/Trunk/AdSdk
    public static final String BUILD = "1.3.6";
    /**
     * 1.2.1:
     * 修改广告加载逻辑，支持各个层级。OOM优化
     * 1.2.2:
     * 增加本地默认配置功能
     * 1.2.3:
     * 判断缓存个数的时候逻辑有bug修改,刷新缓存的时候如果有一个满了就退出bug修改
     * 1.2.4:（maven:1.2.4）
     * 修改打点事件的名称
     * 1.2.4.63:bugfix
     * 本地的默认文件名称修改为default_ad_config.json,调用Show打点的时候，打点有个格式出错。
     * 客户端调用showCustomAdView方法时，fb的show被过滤。打点的对应错误。
     * 1.2.5:(maven:1.2.5)
     * 并行请求广告需求，广告的打点数据漏了雅虎和vk两个平台；
     * 调整整个版本的打点样式，和产品协定的一直。"应用名_广告名_事件名"
     * 1.2.6：(maven:1.2.6,1035,1038,1039,1042,1045,1074,1075,1089,1097,1104)
     * 请求的url中添加ad_sdk_verison字段
     * 支持广告ctr背景样式的修改
     * 1.2.6_1038:添加一些对应的逻辑打点
     * 1.2.7：(maven:1091,1116,1152,1177)
     * 优化缓存刷新的策略，添加新的全屏广告样式15
     * 请求字段增加bid,segment_id 上报参数中增加SEGMENT_ID（AB测试用，通过bid获取到不同的segmentId）
     * 添加字段：flow下面添加weight，修改flow为二维数组类型
     * 增加大图广告样式16,17.增加黑夜白天模式
     * 根据当前充电的电量；广告显示次数、间隔 -- 减少不必要的缓存刷新。
     * 在AdPlacementConfig 中增加 night_mod_time 和day_mod_time字段。
     * 用来获取黑夜模式开始和结束时间
     * adnode  增加字段flow_daily_times 控制flow 1维 展示总数
     * 谷歌FB 支持自定义 标题副标题按钮颜色自定义
     *
     * 合并1.2.6上面的一些功能，内存泄露的优化；
     * 修改可能不受缓存个数限制，会多次请求广告；
     * 预加载功能如果当前缓存中有数据的话，则不进行继续请求。控制广告的请求量。
     * admob的WebView会造成的no package found 的崩溃
     * 支持黑名单功能
     * 修改不支持banner广告的bug。
     * 上传黑名单中的广告是否被使用了还是被浪费的打点
     * fb click show 新增native 信息 上报
     * admob click show 新增native 信息 上报
     * 1.2.8(maven:1.2.8,1198,1206)
     * 电池receiver注册 注销 代码 移动位置。
     * 支持外部自定义模板。
     * 修改fb的素材展示控件为mediaView,补充admob的content类型的icon触发事件。
     * 可以根据slotid控制当前广告位的缓存功能是否关闭
     * fb的上报元素中添加storeUri的字段
     * 上报广告平台请求失败的原因
     * 1.2.9（maven:1.2.9)
     * 配置中各个platForm的openStatus字段生效，控制各个平台是否需要请求广告。
     * 去掉请求到广告数据之后再去缓存的功能
     * 添加上报广告平台请求失败的原因
     * 黑名单复测正常
     * 黑名单字段中加上@SerializedName功能，及时字段被混淆也可以根据这个找到对应的数据，进行解析。
     * 新增百度海外AD_SDK 相关功能！
     * 1.2.10（maven:1269,1273)
     * 添加百度平台
     * 广告的初始化接口，支持广告的请求和黑名单的请求域名分开
     * 添加新样式，22,23,24,25
     * mediaView内存泄露的问题处理，提供一个release的回调接口。
     * 将百度的架包改到1.0.7.1，和fb的架包分开
     * 样式2101界面微调
     * 1.2.11（maven:1285,1286,1288,1289,1294,1302）
     * 增加谷歌插屏广告
     * 将百度的架包改到1.0.8
     * 不再app内部进行主动缓存广告
     * 1.3.1（maven:1300）
     * 新加白名单功能
     * 1.3.2（maven:1302）
     * 提供待机需要的新方法,修改插屏的bug
     * 1.3.3(maven:1307,1310)
     * 如果当前app在使用的时候则不展示插屏广告
     * 1.3.4(maven:1309,1319)
     * 下载广告的碰撞检测，一次请求多个fb广告。新增谷歌视频广告
     * 添加fb的插屏广告
     * 1.3.5（maven:1320,1326,1328）
     * mopub的banner广告回调失败的bug
     * 1.3.6（maven：1332）
     * 支持Admob的NativeExpress类型广告。
     * moup_status--修改为mopub_status
     */
}
