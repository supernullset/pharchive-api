(defproject pharchive-api "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [compojure "1.2.0"]
                 [org.postgresql/postgresql "9.3-1102-jdbc41"]
                 [korma "0.4.0"]
                 [ring/ring-defaults "0.1.2"]
                 [liberator "0.12.2"]
                 [lib-noir "0.9.4"]
                 [cheshire "5.3.1"]]
  :plugins [[lein-ring "0.8.13"]]
  :ring {:handler pharchive-api.core.handler/app}
  :profiles
  {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                        [ring-mock "0.1.5"]]}
   :ragtime {
             :migrations ragtime.sql.files/migrations,
             :database
             "jdbc:postgresql://localhost/pharchive?user=postgres&password=postgres"}}
  )
