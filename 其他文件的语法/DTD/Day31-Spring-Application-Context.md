## summer-context.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE beans SYSTEM "summer-context.dtd">
<beans>
    <alias name="userService" alias="service"/>
    <alias name="userService" alias="service"/>
    <alias name="userService" alias="se"/>
    <import resource="classpath:"/>
    <import resource="classpath:"/>
    <import resource="classpath:"/>
    <import resource="classpath:"/>

    <bean id="userMapper" class="com.harvey.dp.spring.demo.UserMapper"/>
    <bean id="userService" class="com.harvey.dp.spring.demo.UserServiceImpl">
        <property name="userMapper" ref="userMapper"/>
    </bean>
    <bean id="userController" class="com.harvey.dp.spring.demo.UserController" scope="singleton">
        <property name="userService">
            <list>
                <value>mo</value>
                <ref bean="userController"/>
            </list>
        </property>
        <property name="sss">
            <ref bean="userService"/>
        </property>
    </bean>
    <beans>

    </beans>
</beans>
```

## summer-context.dtd

```dtd
<!ELEMENT beans (bean+,alias+,import+,beans+)>
        <!ELEMENT alias EMPTY>
        <!ATTLIST alias name IDREF #REQUIRED>
        <!ATTLIST alias alias CDATA #REQUIRED>


        <!ELEMENT import EMPTY>
        <!ATTLIST import resource CCDATA  #REQUIRED "classpath:">

        <!ELEMENT bean (property+)>
        <!ATTLIST bean id ID #REQUIRED>
        <!ATTLIST bean class CDATA #REQUIRED>
        <!ATTLIST bean scope (singleton|prototype)>
        <!ATTLIST bean name CDATA>


        <!ELEMENT property (list|ref|EMPTY)>
        <!ATTLIST property name CDATA #REQUIRED>
        <!ATTLIST property ref IDREF>
        <!ATTLIST property value CDATA>

        <!ELEMENT ref EMPTY>
        <!ATTLIST ref bean IDREF #REQUIRED>

        <!ELEMENT list ((value+)|(ref+))>
        <!ATTLIST list value-type CDATA>
        <!ELEMENT value (#PCDATA)>
```

