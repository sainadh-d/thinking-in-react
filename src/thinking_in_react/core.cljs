(ns thinking-in-react.core
  (:require [reagent.core :refer [render-component atom]]
            [clojure.string :as str]))

;; (:require [reagent.core :as reagent :refer [atom]]
;; define your app data so that it doesn't get over-written on reload
(defonce app-state
  (atom {:filter-text ""
         :checked false}))

(def PRODUCTS
  [{:category "Sporting Goods"
    :price "$49.99"
    :stocked true
    :name "Football"}

   {:category "Sporting Goods"
    :price "$9.99"
    :stocked true
    :name "Baseball"}

   {:category "Sporting Goods"
    :price "$29.99"
    :stocked false
    :name "Basketball"}

   {:category "Electronics"
    :price "$99.99"
    :stocked true
    :name "iPod Touch"}

   {:category "Electronics"
    :price "$399.99"
    :stocked false
    :name "iPhone 5"}

   {:category "Electronics"
    :price "$199.99"
    :stocked true
    :name "Nexus 7"}])

(defn search-bar []
  [:form
    [:input
     {:type "text"
      :placeholder "Search..."
      :value (:filter-text @app-state)
      :on-change #(swap! app-state assoc :filter-text (.-value (.-target %)))}]
    [:p
      [:input
       {:type "checkbox"
        :checked (:checked @app-state)
        :on-change #(swap! app-state assoc :checked (.-checked (.-target %)))}]
      " "
      [:span
       {:style {:color "green" :fontSize "smaller"}}
       "Only show products in stock"]]])

(defn product-row [product]
  [:tr
   [:td (if (not (:stocked product)) {:style {:color "red"}}) (:name product)]
   [:td {:align "right"} (:price product)]])

(defn show-product [show-in-stock-only product]
  (if (and show-in-stock-only (not (:stocked product)))
    false
    true))

(defn product-table [products]
  [:table {:width "100%"}
   [:thead
    [:tr {:style {:color "blue"}}
     [:th {:align "left"} "Name"]
     [:th {:align "right"} "Price"]]]
   (doall
     (for [[category products] (group-by :category products)]
       (apply conj
         ^{:key category}
         [:tbody
          [:tr
           [:th {:colSpan "2"} category]]]
         (for [product products]
           (let [filter-text (:filter-text @app-state)
                 text-in-name? (str/includes? (:name product) filter-text)]
             (if (and text-in-name?
                   (show-product (:checked @app-state) product))
               [product-row product]))))))])

(defn counter-view [] ;; render fn
  (let [counter (atom 0)] ;; setup
    (fn []
      [:div
       [:p @counter]
       [:button
        {:on-click
         (fn [e]
           (swap! counter inc))}
        "Increment!"]])))


(defn filtertable-product-table [products]
  [:div {:style {:fontFamily "sans-serif"}}
   [search-bar]
   [product-table products]
   [counter-view]])


(defn start []
  (render-component [filtertable-product-table PRODUCTS]
                    (. js/document (getElementById "app"))))

(defn ^:export init []
  ;; init is called ONCE when the page loads
  ;; this is called in the index.html and must be exported
  ;; so it is available even in :advanced release builds
  (start))

(defn stop []
  ;; stop is called before any code is reloaded
  ;; this is controlled by :before-load in the config
  (js/console.log "stop"))

(identity @app-state)
