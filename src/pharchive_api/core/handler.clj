(ns pharchive-api.core.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [liberator.core :refer :all]
            [pharchive-api.db.core :as db]
            [cheshire.core :as json]
            [cemerick.url :refer (url url-encode)]
            [clojure.pprint :refer (pprint)]
          ))

(def protocol "http://")

(def default_limit 10)
(def default_offset 0)

(defn construct-hypermedia-response [metadata data]
  {
   :meta metadata
   :data data})

(defn api-response [meta data]
  (json/generate-string (construct-hypermedia-response meta data)))

(defresource list-collections [uri limit offset]
  :available-media-types ["application/json"]
  :allowed-methods [:get]
  ;; TODO: I need a wrapper around integer parsing with defaults
  :handle-ok (api-response (let [
                                 limit (Integer/parseInt limit)
                                 offset (Integer/parseInt offset)
                                 ] {
                                    :limit  limit
                                    :offset offset
                                    :links  (let [l (merge {
                                                            :self (str (assoc uri :query {:limit limit :offset offset}))
                                                            :first (str (assoc uri :query {:limit limit :offset default_offset}))
                                                            :next (str (assoc uri :query {:limit limit :offset (+ limit offset)}))
                                                            ;; TODO: Add "last" link
                                                            }
                                                           (if (<= 0 (- offset limit))
                                                             {:prev (str (assoc uri :query {:limit limit :offset (- offset limit)}))})
                                                           )]                                              
                                              l)
                                    })
                           ;; Add links to each record
                           (map (fn [r] {:meta {:links {:self (str (assoc uri :path (str (:path uri) "/" (r :uuid))))}}
                                        :data r}) (db/get-all-records :collections limit offset))))

(defresource collection [uri id]
  :available-media-types ["application/json"]
  :allowed-methods [:get]
  :handle-ok (json/generate-string (construct-hypermedia-response {:links {:self (str uri)
                                                                           :all  (str (assoc uri :path "/collections"))}} (db/get-collection id))))


(defroutes app-routes
  (ANY "/collections" [:as req]
       ;; TODO: This is quite dirty
       (let [params (req :params)
             h      (let [u (url (str protocol (get-in req [:headers "host"])))]
                      (assoc u :path (req :uri)))
             l      (params :limit)
             o      (params :offset)
             limit  (if l l (str default_limit))
             offset (if o o (str default_offset))]
         (list-collections h limit offset)))

  (ANY "/collections/:id" [id :as req]
       (let [params (req :params)
             h      (let [u (url (str protocol (get-in req [:headers "host"])))]
                      (assoc u :path (req :uri)))]
         (collection h id)))
  (route/not-found "Not Found"))

(def app
  (wrap-defaults app-routes site-defaults))
