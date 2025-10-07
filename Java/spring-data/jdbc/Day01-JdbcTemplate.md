[JdbcTemplate基本使用](https://blog.csdn.net/weixin_40001125/article/details/88538576)



```java
String sql = "select id,user1,user2 from session_record where user1=" + id + " or user2=" + id;
query(sql, id);
private <T> List<T> query(String sql, T result) {
    List<Map<String, Object>> maps = jdbcTemplate.queryForList(sql);
    return maps.stream().map((map) -> BeanUtil.fillBeanWithMap(map, result, false)).collect(Collectors.toList());
}
```

