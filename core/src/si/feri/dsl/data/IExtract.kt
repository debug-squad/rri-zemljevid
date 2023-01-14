package si.feri.dsl.data

import si.feri.rrizemljevid.utils.Geolocation

interface IExtract {
    fun extract(
        points: MutableList<Geolocation>,
        lines: MutableList<List<Geolocation>>,
        polygons: MutableList<List<Geolocation>>
    )
}