(ns clj-hmac.core-test
  (:use clojure.test
        clj-hmac.msg
        clj-hmac.core

        clj-json.core
        clojure.data.codec.base64))

(defmacro debug
  [form]
  `(let [result# ~form]
     (println '~form "=>" result#)
     result#))

(defn sign-msg
  [msg key & alg]
  (let [msg (-> msg (assoc :algorithm (hmac-algorithm alg)) (dissoc :signature))
        sig (hmac (debug (msg-string msg)) key)]

    (assoc msg :signature (debug (String. (encode sig) "UTF-8")))))


(deftest test-verification-after-json
  (let [key (.getBytes "key" "UTF-8")
        msg (sign-msg {:msg_id "Foo" :data {:type "vector" :content [1 2 3]}} key)
        json-decoded (-> msg generate-string (parse-string true))]

    (is (= json-decoded msg))
    (is (= (:signature json-decoded) (:signature (sign-msg json-decoded key
                                                           (:algorithm json-decoded)))))))