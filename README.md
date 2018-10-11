## 架 构 Architecture

示例环境 -- Amazon EC2 环境包括1个master节点和4个worker节点。

示例Spring Boot应用 -- 包括一个web layer和一个service layer，每个应用都是一个独立的Spring Boot application。

![Architecture](https://raw.githubusercontent.com/rickiechina/springboot-k8s/master/architecture.png)

## Service Deployment Into Kubernetes

将服务部署到Kubernetes集群

首先，编写相应的deployment和service yaml 文件。

### zip-service-deployment.yaml

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: zip-service-deployment
  namespace: default
  labels:
    app: zip-service
spec:
  replicas: 2
  selector:
    matchLabels:
      app: zip-service
  template:
    metadata:
      labels:
        app: zip-service
    spec:
      containers:
      - name: zip-service
        image: registry.cn-hangzhou.aliyuncs.com/rickie/repo:zipcodeservice
        command: ["java"]
        args: ["-jar","/app.jar","8085"]
        ports:
        - containerPort: 8085
```

### zipcode-service.yaml

```yaml
kind: Service
metadata:
  name: zipcode-service
  namespace: default
spec:
  ports:
  - port: 8085
    targetPort: 8085
    name: http
    protocol: TCP
  selector:
    app: zip-service
  type: ClusterIP
```

### zip-web-deployment.yaml

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: zip-web-deployment
  namespace: default
  labels:
    app: zip-web
spec:
  replicas: 2
  selector:
    matchLabels:
      app: zip-web
  template:
    metadata:
      labels:
        app: zip-web
    spec:
      containers:
      - name: zip-web
        image: registry.cn-hangzhou.aliyuncs.com/rickie/repo:webservice
        command: ["java"]
        args: ["-jar","/app.jar","zipcode-service.default.svc.cluster.local:8085","3334"]
        ports:
        - containerPort: 3334
```

### zip-web-service.yaml

```yaml
apiVersion: v1
kind: Service
metadata:
  name: zip-web-service
  namespace: default
spec:
  ports:
  - port: 3334
    targetPort: 3334
    # nodePort: 30000
    name: http
    protocol: TCP
  selector:
    app: zip-web
  type: NodePort
```

其次，在kubernetes master 节点，执行kubectl 指令，进行部署操作。

> kubectl create -f zip-service-deployment.yaml
>
> kubectl create -f zipcode-service.yaml



Deploying the Web Layer -- 部署Web Layer

> kubectl create -f zip-web-deployment.yaml
>
> kubectl create -f zip-web-service.yaml

查看service

> [root@centos-110 zipcode]# kubectl get svc
>
> NAME              TYPE        CLUSTER-IP       EXTERNAL-IP   PORT(S)          AGE
>
> zip-web-service   NodePort    10.102.101.183   <none>        3334:30538/TCP   15h
>
> zipcode-service   ClusterIP   10.104.113.169   <none>        8085/TCP         23d

查看pod

> [root@centos-110 zipcode]# kubectl get pods
>
> NAME                                      READY     STATUS    RESTARTS   AGE
>
> zip-service-deployment-5cf4476c59-2sttv   1/1       Running   1          23d
>
> zip-service-deployment-5cf4476c59-qnqxr   1/1       Running   1          23d
>
> zip-web-deployment-cfcf5d7fd-n56n6        1/1       Running   0          20h
>
> zip-web-deployment-cfcf5d7fd-vhlcl        1/1       Running   0          20h

访问NodePort（Master节点），响应结果如下：

http://192.168.56.110:30538/zip/getZipcodeInfo/33301

![Spring Boot Application Source Code](https://raw.githubusercontent.com/rickiechina/springboot-k8s/master/sourcecode.png)