# REDME

## 概要

このリポジトリは、DDDを採用した場合のマルチプロジェクトの構成を検討しているリポジトリです。

### build.sbtの構成

`build.sbt`を以下のようの構成で定義しています。
ドメイン層を中心として、インフラ層、アプリケーション層はドメインに依存するようにしています。

```build.sbt
lazy val dddOnScala = (project in file("."))
  .aggregate(domain, infra, application, web)
  .settings(/* FIXME */)

// ドメイン層
lazy val domain = (project in file("modules/domain"))
  .settings(/* FIXME */)

// インフラ層
lazy val infra = (project in file("modules/infrastructure"))
  .settings(/* FIXME */)
  .dependsOn(domain)

// アプリケーション層
lazy val application = (project in file("modules/application"))
  .settings(/* FIXME */)
  .dependsOn(domain)

// アダプタ層(web)
lazy val web = (project in file("modules/web"))
  .settings(/* FIXME */)
  .dependsOn(application, infra)
```

## Webアプリケーション系のsbtタスク

WebフレームワークにはPlay Frameworkを利用するように設定しています。

### Playアプリの起動

開発時にアプリケーションを起動する場合。

``` 
sbt web/run
```

### Webアプリのパッケージ化

アプリケーションをパッケージングしてzipファイル化。

```
sbt web/dist
```

### docker imageの作成

dockerイメージ化したい場合。

```
sbt web/docker:publishLocal
```