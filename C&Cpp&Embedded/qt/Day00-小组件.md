# 小组件

处于qt边缘的小组件

虽然不一定是必须的, 不一定是最常用的, 但也有很多有意思的功能

故记录一下API和最简单的用法

## QMessageBox

```cpp
/**
 * @example
 * QMessageBox msgBox;<p>
 * msgBox.setWindowTitle("Take out");<p>
 * msgBox.setText("Food is coming.");<p>
 * msgBox.exec();
 */
QMessageBox msgBox;
QMessageBox::StandardButton button = QMessageBox::information(
        form,
        QApplication::tr("tittle"),
        QApplication::tr("message content")
); // 阻塞, 关闭弹窗界面后进入下面语句
qDebug()<<button; // QMessageBox::Ok
```

![image-20240507160722150](../assetss/Day00-%E5%B0%8F%E7%BB%84%E4%BB%B6/image-20240507160722150.png)

