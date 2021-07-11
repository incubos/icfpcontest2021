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
; Hole
(define-fun h0x () Int 10)
(define-fun h0y () Int 0)
(define-fun h1x () Int 10)
(define-fun h1y () Int 10)
(define-fun h2x () Int 0)
(define-fun h2y () Int 10)
; Vertices
(declare-const v0x Int)
(declare-const v0y Int)
(declare-const v1x Int)
(declare-const v1y Int)
(declare-const v2x Int)
(declare-const v2y Int)
; Edge lengths
(assert (= 100 (squareLength v0x v0y v1x v1y) ))
(assert (= 100 (squareLength v1x v1y v2x v2y) ))
(assert (= 200 (squareLength v2x v2y v0x v0y) ))
; Inside hole
(assert (insideTriangle h0x h0y h1x h1y h2x h2y v0x v0y))
(assert (insideTriangle h0x h0y h1x h1y h2x h2y v1x v1y))
(assert (insideTriangle h0x h0y h1x h1y h2x h2y v2x v2y))
; Solve
(check-sat); Print
(eval v0x)
(eval v0y)
(eval v1x)
(eval v1y)
(eval v2x)
(eval v2y)