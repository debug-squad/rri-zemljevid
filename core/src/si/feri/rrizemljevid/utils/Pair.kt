package si.feri.rrizemljevid.utils

class Pair<A, B>(var fst: A, var snd: B) {
    companion object {
        fun <A, B> of(fst: A, snd: B): Pair<A, B> {
            return Pair(fst, snd)
        }
    }
}