package com.attachme.agent;

import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class OutputParserTest {

  @Test
  public void unixTest() {
    UnixHandler unixHandler = new UnixHandler();
    Function<String, Optional<Integer>> f = unixHandler.outputParser(0);
    Assert.assertFalse(f.apply("COMMAND   PID USER   FD   TYPE             DEVICE SIZE/OFF NODE NAME").isPresent());
    Assert.assertEquals(Optional.of(8084), f.apply("netcat  32133  sme    3u  IPv4 0xc9a87a3a39d8b185      0t0  TCP *:8084 (LISTEN)"));
    Assert.assertEquals(Optional.empty(), f.apply("netcat  32133  sme    3u  IPv4 0xc9a87a3a39d8b185      0t0  TCP *: (LISTEN)"));
  }

  @Test
  public void windowsTest() {
    String[] lines = new String[]{
      "Active Connections\n",
      "\n",
      "  Proto  Local Address          Foreign Address        State           PID\n",
      "  TCP    0.0.0.0:135            0.0.0.0:0              LISTENING       1116\n",
      "  TCP    0.0.0.0:445            0.0.0.0:0              LISTENING       4\n",
      "  TCP    0.0.0.0:3306           0.0.0.0:0              LISTENING       5324\n",
      "  TCP    0.0.0.0:5040           0.0.0.0:0              LISTENING       4808\n",
      "  TCP    0.0.0.0:5700           0.0.0.0:0              LISTENING       4\n",
      "  TCP    0.0.0.0:9012           0.0.0.0:0              LISTENING       15448\n",
      "  TCP    0.0.0.0:33060          0.0.0.0:0              LISTENING       5324\n",
      "  TCP    0.0.0.0:49664          0.0.0.0:0              LISTENING       920\n",
      "  TCP    0.0.0.0:49665          0.0.0.0:0              LISTENING       748\n",
      "  TCP    0.0.0.0:49666          0.0.0.0:0              LISTENING       1636\n",
      "  TCP    0.0.0.0:49667          0.0.0.0:0              LISTENING       2308\n",
      "  TCP    0.0.0.0:49668          0.0.0.0:0              LISTENING       3632\n",
      "  TCP    0.0.0.0:49669          0.0.0.0:0              LISTENING       892\n",
      "  TCP    10.14.74.50:139        0.0.0.0:0              LISTENING       4\n",
      "  TCP    10.14.74.50:49709      40.100.145.162:443     ESTABLISHED     28204\n",
      "  TCP    10.14.74.50:49710      13.107.136.254:443     ESTABLISHED     28204\n",
      "  TCP    10.14.74.50:49711      204.79.197.222:443     ESTABLISHED     28204\n",
      "  TCP    10.14.74.50:49716      172.217.20.198:443     ESTABLISHED     28508\n",
      "  TCP    10.14.74.50:49717      216.58.215.98:443      ESTABLISHED     28508\n",
      "  TCP    10.14.74.50:49718      216.58.215.98:443      ESTABLISHED     28508\n",
      "  TCP    10.14.74.50:49719      212.82.100.176:443     ESTABLISHED     28508\n",
      "  TCP    10.14.74.50:49722      18.195.155.181:443     ESTABLISHED     28508\n",
      "  TCP    10.14.74.50:49723      151.101.13.253:443     ESTABLISHED     28508\n",
      "  TCP    10.14.74.50:49724      34.96.70.1:443         ESTABLISHED     28508\n",
      "  TCP    10.14.74.50:49728      35.190.56.198:443      ESTABLISHED     28508\n",
      "  TCP    10.14.74.50:49732      185.33.223.215:443     TIME_WAIT       0\n",
      "  TCP    10.14.74.50:49738      216.58.215.98:443      ESTABLISHED     28508\n",
      "  TCP    10.14.74.50:49739      151.101.193.69:443     ESTABLISHED     28508\n",
      "  TCP    10.14.74.50:49740      151.101.129.69:443     ESTABLISHED     28508\n",
      "  TCP    10.14.74.50:49741      192.0.73.2:443         ESTABLISHED     28508\n",
      "  TCP    10.14.74.50:49742      104.16.30.34:443       ESTABLISHED     28508\n",
      "  TCP    10.14.74.50:49743      2.18.12.107:443        ESTABLISHED     28508\n",
      "  TCP    10.14.74.50:49744      91.228.74.227:443      ESTABLISHED     28508\n",
      "  TCP    10.14.74.50:54473      51.105.249.228:443     ESTABLISHED     10572\n",
      "  TCP    10.14.74.50:61964      35.190.56.198:443      ESTABLISHED     10620\n",
      "  TCP    10.14.74.50:61973      51.105.249.228:443     ESTABLISHED     3180\n",
      "  TCP    10.14.74.50:61979      64.233.165.188:443     ESTABLISHED     28508\n",
      "  TCP    10.14.74.50:61982      3.120.198.117:443      ESTABLISHED     23604\n",
      "  TCP    10.14.74.50:62540      104.27.86.100:443      ESTABLISHED     28508\n",
      "  TCP    10.14.74.50:62547      104.27.86.100:443      ESTABLISHED     28508\n",
      "  TCP    10.14.74.50:62551      34.95.120.147:443      ESTABLISHED     28508\n",
      "  TCP    10.14.74.50:62552      151.101.114.110:443    ESTABLISHED     28508\n",
      "  TCP    10.14.74.50:62553      162.247.242.19:443     TIME_WAIT       0\n",
      "  TCP    10.14.74.50:62561      23.59.85.48:443        ESTABLISHED     28508\n",
      "  TCP    10.14.74.50:62569      23.59.84.232:443       ESTABLISHED     28508\n",
      "  TCP    10.14.74.50:62578      151.101.114.49:443     ESTABLISHED     28508\n",
      "  TCP    10.14.74.50:62586      23.59.84.247:443       ESTABLISHED     28508\n",
      "  TCP    10.14.74.50:62596      185.29.135.234:443     ESTABLISHED     28508\n",
      "  TCP    10.14.74.50:62599      151.101.14.2:443       ESTABLISHED     28508\n",
      "  TCP    10.14.74.50:62909      69.173.144.136:443     TIME_WAIT       0\n",
      "  TCP    10.14.74.50:62921      37.157.6.245:443       ESTABLISHED     28508\n",
      "  TCP    10.14.74.50:62937      151.101.114.2:443      ESTABLISHED     28508\n",
      "  TCP    10.14.74.50:62942      185.64.189.115:443     TIME_WAIT       0\n",
      "  TCP    10.14.74.50:62961      35.227.248.159:443     ESTABLISHED     28508\n",
      "  TCP    10.14.74.50:62982      185.64.190.80:443      TIME_WAIT       0\n",
      "  TCP    10.14.74.50:62984      185.64.190.80:443      TIME_WAIT       0\n",
      "  TCP    10.14.74.50:62986      185.64.190.80:443      ESTABLISHED     28508\n",
      "  TCP    10.14.74.50:62988      185.64.190.80:443      TIME_WAIT       0\n",
      "  TCP    10.14.74.50:62990      185.64.190.80:443      TIME_WAIT       0\n",
      "  TCP    10.14.74.50:62996      104.17.119.107:443     TIME_WAIT       0\n",
      "  TCP    10.14.74.50:63002      185.64.189.114:443     TIME_WAIT       0\n",
      "  TCP    10.14.74.50:63007      216.58.215.70:443      TIME_WAIT       0\n",
      "  TCP    10.14.74.50:63009      185.64.190.81:443      TIME_WAIT       0\n",
      "  TCP    10.14.74.50:63016      35.190.56.198:443      ESTABLISHED     10620\n",
      "  TCP    10.14.74.50:63020      151.101.13.108:443     ESTABLISHED     28508\n",
      "  TCP    10.14.74.50:63037      37.157.2.234:443       ESTABLISHED     28508\n",
      "  TCP    10.14.74.50:63039      69.173.144.136:443     ESTABLISHED     28508\n",
      "  TCP    10.14.74.50:63042      23.211.161.59:443      ESTABLISHED     28508\n",
      "  TCP    10.14.74.50:63044      54.229.35.82:443       TIME_WAIT       0\n",
      "  TCP    10.14.74.50:63045      104.81.107.117:443     ESTABLISHED     28508\n",
      "  TCP    10.14.74.50:63049      69.173.144.139:443     ESTABLISHED     28508\n",
      "  TCP    10.14.74.50:63052      195.181.174.6:443      ESTABLISHED     28508\n",
      "  TCP    10.14.74.50:63053      13.107.21.200:443      ESTABLISHED     28204\n",
      "  TCP    127.0.0.1:4200         0.0.0.0:0              LISTENING       13684\n",
      "  TCP    127.0.0.1:4200         127.0.0.1:62523        ESTABLISHED     13684\n",
      "  TCP    127.0.0.1:5939         0.0.0.0:0              LISTENING       4328\n",
      "  TCP    127.0.0.1:6942         0.0.0.0:0              LISTENING       8008\n",
      "  TCP    127.0.0.1:8884         0.0.0.0:0              LISTENING       4\n",
      "  TCP    127.0.0.1:27017        0.0.0.0:0              LISTENING       4140\n",
      "  TCP    127.0.0.1:49670        127.0.0.1:49671        ESTABLISHED     5324\n",
      "  TCP    127.0.0.1:49671        127.0.0.1:49670        ESTABLISHED     5324\n",
      "  TCP    127.0.0.1:49682        0.0.0.0:0              LISTENING       12596\n",
      "  TCP    127.0.0.1:49698        0.0.0.0:0              LISTENING       13064\n",
      "  TCP    127.0.0.1:49698        127.0.0.1:49701        ESTABLISHED     13064\n",
      "  TCP    127.0.0.1:49701        127.0.0.1:49698        ESTABLISHED     11352\n",
      "  TCP    127.0.0.1:49712        0.0.0.0:0              LISTENING       11352\n",
      "  TCP    127.0.0.1:51201        127.0.0.1:51202        ESTABLISHED     8008\n",
      "  TCP    127.0.0.1:51202        127.0.0.1:51201        ESTABLISHED     8008\n",
      "  TCP    127.0.0.1:51203        127.0.0.1:51204        ESTABLISHED     8008\n",
      "  TCP    127.0.0.1:51204        127.0.0.1:51203        ESTABLISHED     8008\n",
      "  TCP    127.0.0.1:62523        127.0.0.1:4200         ESTABLISHED     28508\n",
      "  TCP    127.0.0.1:63342        0.0.0.0:0              LISTENING       8008\n",
      "  TCP    [::]:135               [::]:0                 LISTENING       1116\n",
      "  TCP    [::]:445               [::]:0                 LISTENING       4\n",
      "  TCP    [::]:3306              [::]:0                 LISTENING       5324\n",
      "  TCP    [::]:5700              [::]:0                 LISTENING       4\n",
      "  TCP    [::]:33060             [::]:0                 LISTENING       5324\n",
      "  TCP    [::]:49664             [::]:0                 LISTENING       920\n",
      "  TCP    [::]:49665             [::]:0                 LISTENING       748\n",
      "  TCP    [::]:49666             [::]:0                 LISTENING       1636\n",
      "  TCP    [::]:49667             [::]:0                 LISTENING       2308\n",
      "  TCP    [::]:49668             [::]:0                 LISTENING       3632\n",
      "  TCP    [::]:49669             [::]:0                 LISTENING       892\n",
      "  TCP    [::1]:49675            [::]:0                 LISTENING       11392\n",
      "  UDP    0.0.0.0:123            *:*                                    7136\n",
      "  UDP    0.0.0.0:5050           *:*                                    4808\n",
      "  UDP    0.0.0.0:5353           *:*                                    28508\n",
      "  UDP    0.0.0.0:5353           *:*                                    2552\n",
      "  UDP    0.0.0.0:5353           *:*                                    26500\n",
      "  UDP    0.0.0.0:5353           *:*                                    28508\n",
      "  UDP    0.0.0.0:5353           *:*                                    26500\n",
      "  UDP    0.0.0.0:5355           *:*                                    2552\n",
      "  UDP    0.0.0.0:12700          *:*                                    5744\n",
      "  UDP    0.0.0.0:49667          *:*                                    4328\n",
      "  UDP    0.0.0.0:50496          *:*                                    28508\n",
      "  UDP    0.0.0.0:55589          *:*                                    28508\n",
      "  UDP    0.0.0.0:56837          *:*                                    28508\n",
      "  UDP    0.0.0.0:58116          *:*                                    28508\n",
      "  UDP    0.0.0.0:59366          *:*                                    28508\n",
      "  UDP    0.0.0.0:60841          *:*                                    28508\n",
      "  UDP    0.0.0.0:61208          *:*                                    28508\n",
      "  UDP    0.0.0.0:62346          *:*                                    28508\n",
      "  UDP    0.0.0.0:62349          *:*                                    28508\n",
      "  UDP    0.0.0.0:64849          *:*                                    28508\n",
      "  UDP    10.14.74.50:137        *:*                                    4\n",
      "  UDP    10.14.74.50:138        *:*                                    4\n",
      "  UDP    10.14.74.50:1900       *:*                                    5612\n",
      "  UDP    10.14.74.50:2177       *:*                                    11024\n",
      "  UDP    10.14.74.50:56124      *:*                                    5612\n",
      "  UDP    127.0.0.1:1900         *:*                                    5612\n",
      "  UDP    127.0.0.1:5353         *:*                                    4328\n",
      "  UDP    127.0.0.1:49664        *:*                                    4776\n",
      "  UDP    127.0.0.1:56125        *:*                                    5612\n",
      "  UDP    [::]:123               *:*                                    7136\n",
      "  UDP    [::]:5353              *:*                                    2552\n",
      "  UDP    [::]:5353              *:*                                    28508\n",
      "  UDP    [::]:5353              *:*                                    26500\n",
      "  UDP    [::]:5355              *:*                                    2552\n",
      "  UDP    [::]:49668             *:*                                    4328\n",
      "  UDP    [::1]:1900             *:*                                    5612\n",
      "  UDP    [::1]:56123            *:*                                    5612\n",
      "  UDP    [fe80::4eb:b862:d572:9522%3]:1900  *:*                                    5612\n",
      "  UDP    [fe80::4eb:b862:d572:9522%3]:2177  *:*                                    11024\n",
      "  UDP    [fe80::4eb:b862:d572:9522%3]:56122  *:*                                    5612\n"};
    WindowsHandler windowsHandler = new WindowsHandler();
    Function<String, Optional<Integer>> f = windowsHandler.outputParser(13684);
    List<Integer> collect = Stream.of(lines)
      .map(f)
      .filter(Optional::isPresent)
      .map(Optional::get)
      .collect(Collectors.toList());
    Assert.assertEquals(Collections.singletonList(4200), collect);
  }
}
