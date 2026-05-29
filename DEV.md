# DEV Notes

## Requirements

npm
java 21
gradle

## commitlint

Run [commitlint](https://commitlint.js.org/)  configuration script to enforce conventional commit message format during the development. 

```shell
./.commitlint/configure-commitlint.sh
```

## Debugging

Start Burp Suite
```
java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005 -jar "/Applications/Burp Suite.app/Contents/Resources/app/burpsuite.jar"
```

Setup IntelliJ IDEA remote debug
```
-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005
```
