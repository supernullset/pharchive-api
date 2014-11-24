(ns pharchive-api.db.schema)

(def db-spec
  {:subprotocol "postgresql"
   :subname "//localhost/pharchive"
   :user "postgres"
   :password "postgres"})

(def test-db-schema
  {:subprotocol "postgresql"
   :subname "//localhost/pharchive-test"
   :user "postgres"
   :password "postgres"})


