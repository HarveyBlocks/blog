# Group By 优化

```mysql
create index profession_age on 
	user(
    	profession,
        age
    );
```

-   

``` Mysql
select profession,age from user where  profession = 'Math' and age >20;# OK
select profession,age from user group by profession  and age  ;# OK
select profession,age from user group by   age ; # Not OK
select profession,age from user where profession = 'Math' group by age >20;# OK
```

