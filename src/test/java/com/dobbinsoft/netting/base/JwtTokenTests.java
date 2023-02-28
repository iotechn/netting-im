package com.dobbinsoft.netting.base;

import com.dobbinsoft.netting.base.utils.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class JwtTokenTests {

    private String hmac256Secret;

    private String userId;

    private String hmac256JwtToken;

    private String rsa256PublicKey =
            "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAw8sKY7CIKhIkL2TW6S/Q" +
            "MbYq61/8tpZNrzG3SREA8zYUMRssdE/nJtuctgg88F2wlNcr+/8bVP3VMwN2ZT+u" +
            "SHC8H2UyDMu9br94JieBACsAss1ktDvIDqUhpj/ZMMKVWrfje2iKiMBx8Go0cCZl" +
            "DsPj/8n5JuRkYIHZdVR+NYoOmaEdZlS8oD5PZyR54tHNS+RkOOjixbA+2XRyYQeg" +
            "oWN3pEjL1TEkSgT9eiXzixmDoAD+ugorCXzp6iKyLCDtt5wqU0IOKoMTsSUh4B07" +
            "rG7HTVkxq0mn/xegl+ejAn+XSDuJLv6xYiH9fpb+Ypk9uFS2+1p+GMv/7VzWNMKr" +
            "YwIDAQAB";

    private String rsa256PrivateKey =
            "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQDDywpjsIgqEiQv" +
            "ZNbpL9AxtirrX/y2lk2vMbdJEQDzNhQxGyx0T+cm25y2CDzwXbCU1yv7/xtU/dUz" +
            "A3ZlP65IcLwfZTIMy71uv3gmJ4EAKwCyzWS0O8gOpSGmP9kwwpVat+N7aIqIwHHw" +
            "ajRwJmUOw+P/yfkm5GRggdl1VH41ig6ZoR1mVLygPk9nJHni0c1L5GQ46OLFsD7Z" +
            "dHJhB6ChY3ekSMvVMSRKBP16JfOLGYOgAP66CisJfOnqIrIsIO23nCpTQg4qgxOx" +
            "JSHgHTusbsdNWTGrSaf/F6CX56MCf5dIO4ku/rFiIf1+lv5imT24VLb7Wn4Yy//t" +
            "XNY0wqtjAgMBAAECggEAYJxFih9HcQr2k/Q8H2AaaQ0sbGw2tJnBx1rOx9z+DQR4" +
            "PircaqAqvP/Mef3io/B2+1qZN4UPpYaCtuNUfm1HwDLwgMVcRvgZhxYvYRRJER4n" +
            "ABNdR5ngA2n0Cqaisu6w58ypEPHqucPL9QdSzKwFXZ3HWSX+LPSxmvU3qi+A/0U8" +
            "m0i5mCRJNHIvQtAr0rggtWoGjcAtOomogNpclIqXEGnCqkEaH82qmaUmzsmcZrBS" +
            "WHJIE8Brtc8EWlbMhStREXG6+mN6Z4BScKPfER7xpxELNIUKMIViZnT0kX120uEb" +
            "pgqhDdUc2YvMir9i2Nja8COQ9n5p+0v7c52VCSzTkQKBgQDwzLFgFTnNwH80HvAK" +
            "SGJpKGyUBP3aXU598MjT9XzptOHR4icAm8I8lJ1t2oLqSMud29NSoLt7BCdlqU8v" +
            "cFu680nYVLBRt9tIjXo4cKVrEG8I5acb1avt2A5DDBE3QBfxce9JZrXPnxXPlqt1" +
            "Rp0b+zQX+blGHyqGJzhw3bqS3wKBgQDQJwvJyULSLeLT3k3Pkw60ekX0xzx4Bekt" +
            "xVtBld6RaHhwq8McZEtPUt0FTxusJOWpiEs+ZLL1880p3nrTFcPZw87//t3SdqfH" +
            "NIbtIec5U8OwHI0oXLx/ru8vjGxStZCmhk76w4B7LRssGL+2tLG8Pi0i2Gf6vHwi" +
            "1uagiF4b/QKBgQCAVZdzDdzwu1cQAC0OTeWlBdZYTRC+AFyE4n8l9//KzoxLv6vp" +
            "txmeD9aXZY3AL2vVhgLqVp+UwHH+zG1Xaxp4WJFSqlAOPV6xHDW3PtHqbae/piWZ" +
            "wBcRe8SeFyBVweMA2ygchpD/RlnpePOIXUn+T7ND0+Paa2HOfbim7oUxIQKBgQCB" +
            "NdvBdOe/5LALaNHQaq8UJVrLqenFb5XhzcLqIYg5pyosdtL1CuUc5olAxGyfHYBb" +
            "Drn2jzfwKVlQBkUkRzx+L2iNzL4R6YoYxGiFRyhrk63D4tVVGswATq03TOnpJcAX" +
            "PwRD8ZwN2mAlrW9EkUJLYiVOnhtdsCc20UP0nvIcWQKBgDt0ARmHFoneWhO0wl2o" +
            "CbujihkoXchwlaTxN3Oa8BqvR9iMGJpFJQvZgbx9CLi8TlTJqgLsNPCwhnv/tu3y" +
            "Qo8V0fDeaIXx9j0td7JWYsHDX5WFvYnLZvSKUem46k6mNHOGnegS3O4fadMP5Rx6" +
            "Kh1u5lxVNSitOnIDLbIIzYKO";

    @Before
    public void before() {
        hmac256Secret = UUID.randomUUID().toString();
        userId = "TestUser";
        log.info("Secret:{}", hmac256Secret);
        Map<String, String> payload = new HashMap<>();
        payload.put("userId", userId);
        hmac256JwtToken = JwtUtils.createHMAC256(new HashMap<String, String>(), payload, 5, hmac256Secret);
    }

    @Test
    public void aCreateToken() {
        Map<String, String> payload = new HashMap<>();
        payload.put("userId", userId);
        String token = JwtUtils.createHMAC256(new HashMap<String, String>(), payload, 3600, hmac256Secret);
        log.info("JwtToken:{}", token);
    }

    @Test
    public void bParseTokenSuc() {
        JwtUtils.JwtResult verify = JwtUtils.verifyHMAC256(this.hmac256JwtToken, hmac256Secret);
        log.info("JwtToken Suc: {}", verify);
    }

    @Test
    public void cParseTokenFail() {
        JwtUtils.JwtResult verify = JwtUtils.verifyHMAC256(this.hmac256JwtToken, hmac256Secret + "ABC");
        log.info("JwtToken Fail: {}", verify);
    }

    @Test
    public void dParseTokenExpire() throws Exception{
        Thread.sleep(6000);
        JwtUtils.JwtResult verify = JwtUtils.verifyHMAC256(this.hmac256JwtToken, hmac256Secret);
        log.info("JwtToken Expire: {}", verify);
    }

    @Test
    public void eCreateRsa256Token() throws Exception {
        Map<String, String> payload = new HashMap<>();
        payload.put("userId", userId);
        String token = JwtUtils.createRSA256(new HashMap<>(), payload, 10, rsa256PrivateKey);
        log.info("JwtToken Rsa256 token: {}", token);
        JwtUtils.JwtResult result = JwtUtils.verifyRSA256(token, rsa256PublicKey);
        log.info("JwtToken Rsa256 verify: {}", result);
    }



}
