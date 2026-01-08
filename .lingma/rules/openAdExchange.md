---
trigger: always_on
---

1、将open-adx所有模块当做模型上下文输入
2、严格按照指令执行，不做多余的事情
3、不要引入mapstruct这个依赖，任何模块都不需要
- 在终端执行 maven命令的时候，不要使用mvn命令，使用./mvnw命令
- maven install模块的时候，默认跳过测试，跳过 gpg签名
- 生成测试类的时候禁止使用SpringBootTest注解
- 针对使用依赖的单元测试，如果要求必须使用真实依赖的话，请使用ContextConfiguration注解