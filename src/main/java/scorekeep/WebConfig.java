package scorekeep;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import javax.servlet.Filter;
import com.amazonaws.xray.AWSXRay;
import com.amazonaws.xray.AWSXRayRecorderBuilder;
import com.amazonaws.xray.javax.servlet.AWSXRayServletFilter;
import com.amazonaws.xray.plugins.EKSPlugin;
import com.amazonaws.xray.plugins.ElasticBeanstalkPlugin;
import com.amazonaws.xray.strategy.sampling.LocalizedSamplingStrategy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
public class WebConfig {
  private static final Logger logger = LoggerFactory.getLogger(WebConfig.class);

  //XRayServletFilter
  //受信するHTTPリクエストをトレース
  @Bean
  public Filter TracingFilter() {
    return new AWSXRayServletFilter("Scorekeep");
  }

  @Bean
  public Filter SimpleCORSFilter() {
    return new SimpleCORSFilter();
  }

  static {
    if ( System.getenv("NOTIFICATION_EMAIL") != null ){
      try { Sns.createSubscription(); }
      catch (Exception e ) {
        logger.warn("Failed to create subscription for email "+  System.getenv("NOTIFICATION_EMAIL"));
      }
    }
    //EKSPluginを使用するために、AWSXRayRecorderBuilderのwithPluginを呼び出す
    AWSXRayRecorderBuilder builder = AWSXRayRecorderBuilder.standard().withPlugin(new EKSPlugin());

    //ルールファイルを取得
    URL ruleFile = WebConfig.class.getResource("/sampling-rules.json");
    builder.withSamplingStrategy(new LocalizedSamplingStrategy(ruleFile));

    AWSXRay.setGlobalRecorder(builder.build());
  }
}
