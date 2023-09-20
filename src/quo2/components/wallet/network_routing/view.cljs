(ns quo2.components.wallet.network-routing.view
  (:require
   [quo2.foundations.colors :as colors]
   [react-native.core :as rn]
   [react-native.gesture :as gesture]
   [react-native.reanimated :as reanimated]
   [oops.core :as oops]
   [reagent.core :as reagent]))

(defn slider
  [slider-width slider-height]
  (let [appearing-opacity (reanimated/use-shared-value 0)]
    (rn/use-effect
     (fn []
       (reanimated/animate appearing-opacity 1 300)
       ;; TODO: think how to disappear the component animated
       #(reanimated/animate appearing-opacity 0 300)))

    [rn/view {:style {:width            28
                      :background-color :transparent
                      :justify-content  :center
                      :align-items      :center
                      :right            -14}}
     [reanimated/view
      {:style (reanimated/apply-animations-to-style
               {:opacity appearing-opacity
                :height  slider-height
                :width   slider-width}
               {:background-color colors/white
                :height           32
                :width            4
                :border-radius    4})}]]))

(defn network-bar []
  (let [show-slider?          (reagent/atom false)
        initial-gesture-width (reagent/atom 0)]
    (fn [{:keys [max-width selected-width]}]
      (let [slider-width  (reanimated/use-shared-value 4)
            slider-height (reanimated/use-shared-value 32)
            ;;

            ]
        [reanimated/view
         {:style {:width            (if @show-slider?
                                      max-width
                                      (reanimated/get-shared-value selected-width))
                  :background-color "#758EEB40"
                  :flex-direction   :row
                  :justify-content  :space-between
                  ;;
                  ;:background-color :red
                  }
          #_(reanimated/apply-animations-to-style
             {:width selected-width}
             {:width            (if @show-slider?
                                  max-width
                                  (reanimated/get-shared-value selected-width))
              :background-color "#758EEB40"
              :flex-direction   :row
              :justify-content  :space-between})}

         [rn/touchable-without-feedback {:on-press #(swap! show-slider? not)}
          [reanimated/view
           {:style (reanimated/apply-animations-to-style
                    {:width selected-width}
                    {:max-width        max-width
                     :flex-direction   :row
                     :justify-content  :flex-end
                     :background-color "#758EEB"})}
           (when @show-slider?
             [gesture/gesture-detector
              {:gesture (-> (gesture/gesture-pan)
                            (gesture/enabled @show-slider?)
                            (gesture/on-begin
                             (fn [_]
                               (reanimated/animate slider-width 8 300)
                               (reanimated/animate slider-height 40 300)
                               ;;
                               (reset! initial-gesture-width
                                       (reanimated/get-shared-value selected-width))))
                            (gesture/on-update
                             (fn [event]
                               (let [new-pos (+ (oops/oget event "translationX")
                                                @initial-gesture-width
                                                ;(oops/oget event "x")
                                                ;(reanimated/get-shared-value selected-width)
                                                )]
                                 ;(prn (oops/oget event "x"))
                                 #_(println (reanimated/get-shared-value selected-width)
                                            " + "
                                            (oops/oget event "x")
                                            " = "
                                            new-pos)
                                 (reanimated/set-shared-value selected-width new-pos))
                               nil))
                            (gesture/on-end
                             (fn [_]
                               (reanimated/animate slider-width 4 300)
                               (reanimated/animate slider-height 32 300))))}
              [:f> slider slider-width slider-height]])]]

         (when @show-slider?
           [reanimated/view
            {:style {:border-left-width 1
                     :border-style      :dashed
                     :border-color      "#758EEB"
                     :z-index -1}}])
         ])
      )))

(defn view-internal []
  (let [selected-width (reanimated/use-shared-value 110)]
    [rn/view {:style {;:margin-horizontal 20
                      :flex-direction   :row
                      :height           64
                      :background-color :lightgrey
                      :border-radius    18 ;; TODO: check border-radius
                      :overflow         :hidden}}
     [:f> network-bar {:max-width      160
                       :selected-width selected-width}]

     #_#_#_#_#_[rn/view {:style {:width            "33.3%"
                                 :background-color :blue}}]
             [rn/view {:style {:width 1}}]
             [rn/view {:style {:width            "33.3%"
                               :background-color :red}}]
             [rn/view {:style {:width 1}}]
             [rn/view {:style {:width            "33.3%"
                               :background-color :skyblue}}]
     ]))

(defn view []
  [:f> view-internal])
