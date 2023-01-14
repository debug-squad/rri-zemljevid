package si.feri.dsl.data.geometry

import si.feri.dsl.data.IExtract
import si.feri.dsl.data.IToDSL

@kotlinx.serialization.Serializable
abstract sealed class IGeometryObject : IToDSL, IExtract