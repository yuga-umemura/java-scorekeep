## Configuration

### 1. Service plugins<br>
プラグインを使用して、アプリケーションをホストするサービスに関する情報を記録する

* EKSPluginは、コンテナID、クラスタ名、ポッドID、およびCloudWatch Logs Groupを追加する

WebConfig.java

```
import com.amazonaws.xray.AWSXRay;
import com.amazonaws.xray.AWSXRayRecorderBuilder;
import com.amazonaws.xray.plugins.EKSPlugin;
import com.amazonaws.xray.strategy.sampling.LocalizedSamplingStrategy;

@Configuration
public class WebConfig {
...
  static {
    AWSXRayRecorderBuilder builder = AWSXRayRecorderBuilder.standard().withPlugin(new EC2Plugin()).withPlugin(new ElasticBeanstalkPlugin());

    URL ruleFile = WebConfig.class.getResource("/sampling-rules.json");
    builder.withSamplingStrategy(new LocalizedSamplingStrategy(ruleFile));

    AWSXRay.setGlobalRecorder(builder.build());
  }
}
```
### 2. Sampling Rules
* SDKは、X-RayコンソールorJSONで定義した<span style="color: orange; ">**サンプリングルール**</span>を使用して、記録するリクエストを決定しする。
* デフォルトのルールでは、最初のリクエストを毎秒トレースし、X-Rayにトレースを送信するすべてのサービスにおいて追加のリクエストの5％をトレースする。
* X-Rayコンソールで追加のルールを作成し、アプリケーションごとに記録されるデータ量をカスタマイズする。
* SDKは、カスタムルールを定義された順番で適用する。
* リクエストが複数のカスタムルールに一致する場合、SDKは最初のルールのみを適用する。
* Lambdaではサンプリングレートを変更できない
* Springでバックアップルールを提供するには、設定クラスで<span style="color: orange; ">CentralizedSamplingStrategy</span>を持つグローバルレコーダを構成します。
 

### 3. Logging<br>
デフォルトではSDKはアプリケーションのログにERRORレベルのメッセージを出力する
