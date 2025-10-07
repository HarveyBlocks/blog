# 数组

```shell
#!/usr/bin/env sh
nums=( 1 2 3 2 3 1 )
for i in {0..20}; do
    echo ${nums[$i]}
done
nums[2]=10
echo ${nums[2]}
nums[10]=10
echo ${nums[2]}
for i in {0..20}; do
    echo ${nums[$i]}
done
```

