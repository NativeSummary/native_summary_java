# native_summary_java

Generate body for native methods in APK. Java part of native_summary project.

build with maven.

Usage: see APKRepacker.main

### develop

install maven https://maven.apache.org/install.html

To debug with source: `mvn dependency:sources` and reload vscode window.

### JNI to Java operations

|       JNI        | Java |
|:----------------:|----|
| Call<Ty>Method   |  O  |
| Set/Get<Ty>Field |  O  |
| GetString[UTF]Chars|  O  |
| NewString[UTF]   |  O  |
| NewObject        |  O  |
| Throw[New]       |  O  |
| Get/SetObjectArrayElement  |  O  |
| GetObjectClass       |  O  |


## common errors

- `Load semantic summary failed`: binary analyzer cannot produce any summary IR file.

apk is packed:
- `cannot set active body for phantom class` / `Cannot find class (PhantomClass), probably the apk is packed?` apk probably is packed by a packer, and the real dex code is hidden.

Some error caused by soot: (by manual checking)
- `Trying to cast reference type java.lang.Object to a primitive`
- `java.lang.RuntimeException: not found: null_type` (sort of)
- `Unexpected inner class annotation element`
- `Trying to cast reference type java.lang.Object to a primitive`
- `Caused by: java.lang.IllegalStateException: UnitThrowAnalysis StmtSwitch: type of throw argument is not a RefType!`
