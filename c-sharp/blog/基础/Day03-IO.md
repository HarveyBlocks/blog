# IO

API略

## resource

```csharp
using (resource) {

}
```

```csharp
using (FileStream fs = new FileStream("aaa", FileMode.Open, FileAccess.Read)) {
    int data = fs.ReadByte();
}
```

或

```csharp
using FileStream fs = new FileStream("aaa", FileMode.Open, FileAccess.Read);
int data = fs.ReadByte();
```

