(ns pharchive-api.core.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [liberator.core :refer :all]
            [pharchive-api.db.core :as db]
            [cheshire.core :as json]
          ))

(def foo "bar")

(defresource list-collections []
  :available-media-types ["application/json"]
  :allowed-methods [:get]
  :handle-ok (json/generate-string (db/get-all-records :collections)))

(defresource collection [id]
  :available-media-types ["application/json"]
  :allowed-methods [:get]
  :handle-ok (json/generate-string (db/get-collection (Integer/parseInt id))))


(defroutes app-routes
  (ANY "/collections" []
       (list-collections))
  (ANY "/collection/:id" [id]
       (collection id))
  (route/not-found "Not Found"))

(def app
  (wrap-defaults app-routes site-defaults))
