; Functions

(define-fun squareLength
    ((ax Int) (ay Int) (bx Int) (by Int)) Int
    (+
        (*
            (- ax bx)
            (- ax bx))
        (*
            (- ay by)
            (- ay by))))

; Inside trinagle: https://abakbot.ru/online-2/280-pointreug
; http://primat.org/publ/lezhit_li_tochka_vnutri_treugolnika/8-1-0-1147

(define-fun insideUtil
    ((ax Int) (ay Int) (bx Int) (by Int) (xx Int) (xy Int)) Int
    (-
        (*
            (- ax xx)
            (- by ay))
        (*
            (- bx ax)
            (- ay xy))))

(define-fun insideCheck
    ((e1 Int) (e2 Int) (e3 Int)) Bool
    (or
        (and (<= 0 e1) (and (<= 0 e2) (<= 0 e3)))
        (and (>= 0 e1) (and (>= 0 e2) (>= 0 e3))))
    )

(define-fun insideTriangle
    ((ax Int) (ay Int) (bx Int) (by Int) (cx Int) (cy Int) (xx Int) (xy Int)) Bool
    (insideCheck
            (insideUtil ax ay bx by xx xy)
            (insideUtil bx by cx cy xx xy)
            (insideUtil cx cy ax ay xx xy)))
