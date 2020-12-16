(ns crisco.web
  (:require
    [compojure.core :refer [defroutes GET PUT POST DELETE ANY]]
    [compojure.handler :refer [site]]
    [compojure.route :as route]
    [clojure.java.io :as io]
    [ring.middleware.stacktrace :as trace]
    [ring.middleware.params :as p]
    [ring.adapter.jetty :as jetty]
    [environ.core :refer [env]]
    [crisco.data :as data]))

;(defn- authenticated? [user pass]
;  ;; TODO: heroku config:add REPL_USER=[...] REPL_PASSWORD=[...]
;  (= [user pass] [(env :repl-user false) (env :repl-password false)]))
;
;(def ^:private drawbridge
;  (-> (drawbridge/ring-handler)
;      (session/wrap-session)
;      (basic/wrap-basic-authentication authenticated?)))

(defroutes routes
           ;(ANY "/repl" {:as req}
           ;  (drawbridge req))
           (GET "/" []
             {:status  200
              :headers {"Content-Type" "text/plain"}
              :body    (slurp (io/resource "index.html"))})
           (GET "/gh" []
             {:status 301
              :headers {"Location" "http://github.com"}})
           (GET "/:slug" [slug]
             {:status 301
              :headers {"Location" (data/request-redirect! [slug])}})
           (POST "/shorten/:slug" [slug target]
             (if (data/store-slug! slug target)
               {:status 200}
               {:status 409}))
           (ANY "*" []
             (route/not-found (slurp (io/resource "404.html")))))

(defn wrap-error-page [handler]
  (fn [req]
    (try (handler req)
         (catch Exception e
           {:status  500
            :headers {"Content-Type" "text/html"}
            :body    (slurp (io/resource "500.html"))}))))

(def app (-> routes
             p/wrap-params))

(defn wrap-app [app]
  ;; TODO: heroku config:add SESSION_SECRET=$RANDOM_16_CHARS
  (-> app
      ((if (env :production)
         wrap-error-page
         trace/wrap-stacktrace))))

(defn -main [& [port]]
  (let [port (Integer. (or port (env :port) 5000))]
    (jetty/run-jetty (wrap-app #'app) {:port port :join? false})))

;; For interactive development:
;; (.stop server)
;; (def server (-main))
