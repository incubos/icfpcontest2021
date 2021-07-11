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

(define-fun direction
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
            (direction ax ay bx by xx xy)
            (direction bx by cx cy xx xy)
            (direction cx cy ax ay xx xy)))

(define-fun edgeIntersectsTriangle
    ((ax Int) (ay Int) (bx Int) (by Int) (cx Int) (cy Int) (x1x Int) (x1y Int) (x2x Int) (x2y Int)) Bool
    (or (and
            (> 0 (*
                (direction ax ay bx by x1x x1y)
                (direction ax ay bx by x2x x2y)))
            (> 0 (*
                (direction x1x x1y x2x x2y ax ay)
                (direction x1x x1y x2x x2y bx by))))
        (or
            (and
                (> 0 (*
                    (direction bx by cx cy x1x x1y)
                    (direction bx by cx cy x2x x2y)))
                (> 0 (*
                    (direction x1x x1y x2x x2y bx by)
                    (direction x1x x1y x2x x2y cx cy))))
            (and
                (> 0 (*
                    (direction cx cy ax ay x1x x1y)
                    (direction cx cy ax ay x2x x2y)))
                (> 0 (*
                    (direction x1x x1y x2x x2y cx cy)
                    (direction x1x x1y x2x x2y ax ay)))))))
