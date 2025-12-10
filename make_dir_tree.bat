@echo off
setlocal enabledelayedexpansion

:: 清空输出文件
type nul > dir-tree.txt

:: 遍历所有.md文件
for /r %%f in (*.md) do (
    set "fullpath=%%f"
    set "relpath=!fullpath:%cd%\=!"
    
    :: 将路径转换为树状结构
    set "treePath="
    for %%a in (!relpath:\= !) do (
        if "!treePath!"=="" (
            set "treePath=%%a"
        ) else (
            set "treePath=!treePath!\!%%a"
        )
        echo !treePath! >> temp.txt
    )
)

:: 去重并排序
sort temp.txt /unique > dir-tree.txt
del temp.txt