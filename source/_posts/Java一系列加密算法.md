---
title: Java一系列加密算法
date: 2017-05-24 13:32:23
tags: Java
---

Java中有自带的加密算法,比如sha1,md5等.通过MessageDigest来实现.

<!--more-->

# sha1,md5
这种加密算法实现基本上一样,这里用sha1来举例.

首先获取sha-1的MessageDigest实例:
```
MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
```
之后通过messageDigetst.update()方法,将读取的数据依次传入,或者将数据的bytes字节整体传入.
```
messageDigest.update("tmp".getBytes());
```

最后通过digest()获取加密后的字节数组:
```
byte[] result = messageDigest.digest();
```

那么将字节数组转换为16进制字符串的方式为:
```
new BigInteger(1,result).toString(16).toUpperCase();
```

所以整体代码是:
```
    public static String sha2hex(String input) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
        messageDigest.update(input.getBytes());
        byte[] result = messageDigest.digest();
        return new BigInteger(1, result).toString().toUpperCase();
    }
```

转换为16进制字符串还有一种方法:
```
	StringBuilder sb = new StringBuilder();
    for (byte now : result) {
        int t = now & 0xFF;
        if (t < 16) sb.append("0");
        sb.append(Integer.toString(t, 16).toUpperCase());
    }
    return sb.toString();
```

# 对称密码
对称密码是指加密解密均使用相同的密钥.以AES举例.
Java中`Cipher`是加密算法的超类,需要通过getInstance来获取某个加密算法:
```
Cipher cipher = Cipher.getInstance("AES");
```

之后,调用`cipher.init(mode,key)`,其中mode是模式,模式常用两种:`Cipher.ENCRYPT.MODE`加密模式,`Cipher.DECRYPT_MODE`解密模式.

而key则是使用生成器生成.其生成步骤为:
```
KeyGenerator keygen = KeyGenerator.getInstance("AES");
SecureRandom random = new SecureRondom(); 
keygen.init(random);
Key key = keygen.generateKey();
```
`SecureRandom`是一种相对安全的随即数.

那么将某一字符串加密过程就是:
```
byte[] outBytes = cipher.doFinal("aaa".getBytes());
```
而将加密后的字节数组解密就是:
```
cipher.init(Cipher.DECRYPT_MODE,key);
byte[] inBytes = cipher.doFinal(outBytes);
```


# 公共密钥密码
公共密钥密码是一种公共密钥 + 私有密钥进行加密解密的方式.
以RSA举例,如果要使用该算法,需要一对公共/私有密钥.:
```
	KeyPairGenerator keygen = KeyPairGenerator.getInstance("RSA");
    SecureRandom random = new SecureRandom();
    keygen.initialize(1024,random);  //注意第一个表示key的位大小,最小是512
    KeyPair keyPair = keygen.generateKeyPair();
    Key publicKey = keyPair.getPublic();
    Key privateKey = keyPair.getPrivate();
```

生成公共密钥后,在加密的时候,需要将公共密钥初始化到Cipher对象中,而解密的时候,需要将似有密钥初始化到Cipher中.

如:
```
	//以下代码进行加密
	String input = "abcd";
	byte[] out = cipher.doFinal(input.getBytes());
	System.out.println(new BigInteger(1,out).toString(16).toUpperCase());

	//以下代码进行解密
	cipher.init(Cipher.DECRYPT_MODE,privateKey);
	byte[] source = cipher.doFinal(out);
	System.out.println(new String(source));
```
最后打印的结果:
```
1E76DE31E9D9E689AB57E737C878EBF603A8B68769DD63F3E2DDEAC1F35EA803CF0407D913CC0D5F8D09288E89B666F7D352C0B1438C7C3C9E4C998BF83C8021B12B056E96AF617EC8A5B14EC0F558EC24671DAE78288E61CB5C45131977208D8EDDD82FEC715C428E45F21E99A782BAF3DCE8DBAD3EF4798EB3BAD74576A232
abcd
```

网上还有有关RSA的工具类,这里就不贴了.

