.class public Output 
.super java/lang/Object

.method public <init>()V
 aload_0
 invokenonvirtual java/lang/Object/<init>()V
 return
.end method

.method public static print(I)V
 .limit stack 2
 getstatic java/lang/System/out Ljava/io/PrintStream;
 iload_0 
 invokestatic java/lang/Integer/toString(I)Ljava/lang/String;
 invokevirtual java/io/PrintStream/println(Ljava/lang/String;)V
 return
.end method

.method public static read()I
 .limit stack 3
 new java/util/Scanner
 dup
 getstatic java/lang/System/in Ljava/io/InputStream;
 invokespecial java/util/Scanner/<init>(Ljava/io/InputStream;)V
 invokevirtual java/util/Scanner/next()Ljava/lang/String;
 invokestatic java/lang/Integer.parseInt(Ljava/lang/String;)I
 ireturn
.end method
.method public static run()V
 .limit stack 1024
 .limit locals 256
 invokestatic Output/read()I
 istore 0
L1:
 invokestatic Output/read()I
 istore 1
L2:
 iload 0
 ldc 1
 if_icmpne L4
L5:
 ldc 111111
 invokestatic Output/print(I)V
L6:
 goto L3
L4:
 iload 0
 ldc 2
 if_icmpne L7
L8:
 ldc 22222
 invokestatic Output/print(I)V
L9:
 goto L3
L7:
 ldc 5
 ldc 4
 iadd 
 ldc 7
 imul 
 ldc 9
 iadd 
 ldc 0
 iadd 
 istore 0
L3:
 ldc 0
 istore 2
L10:
L12:
 iload 0
 iload 1
 if_icmpge L11
L13:
 iload 0
 ldc 1
 iadd 
 istore 0
L14:
L16:
 iload 2
 ldc 3
 if_icmpge L15
L17:
 iload 0
 invokestatic Output/print(I)V
L18:
 iload 2
 ldc 1
 iadd 
 istore 2
L19:
 goto L16
L15:
 ldc 0
 istore 2
L20:
 goto L12
L11:
L0:
 return
.end method

.method public static main([Ljava/lang/String;)V
 invokestatic Output/run()V
 return
.end method

