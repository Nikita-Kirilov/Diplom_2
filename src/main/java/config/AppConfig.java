package config;

import org.aeonbits.owner.Config;

@Config.LoadPolicy(Config.LoadType.MERGE)
@Config.Sources({"system:properties", "classpath:config.qa.properties"})

public interface AppConfig extends Config {
    @DefaultValue("https://stellarburgers.nomoreparties.site")
    String baseUrl();
}

