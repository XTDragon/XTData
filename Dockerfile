#依赖的父镜像
FROM openjdk:8
#jar包添加到镜像中
ADD XTData-0.0.1-SNAPSHOT.jar  ./XTData.jar
#容器暴露的端口 即jar程序在容器中运行的端口
EXPOSE 9090
#容器启动之后要执行的命令
ENTRYPOINT ["java","-jar","XTData.jar"]
