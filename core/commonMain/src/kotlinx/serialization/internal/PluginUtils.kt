package kotlinx.serialization.internal

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.MissingFieldException
import kotlinx.serialization.descriptors.SerialDescriptor

@OptIn(ExperimentalSerializationApi::class)
@InternalSerializationApi
public fun throwMissingFieldException(seen: Int, goldenMask: Int, descriptor: SerialDescriptor) {
    val missingFields = mutableListOf<String>()
    var seenBits = seen
    var goldenMaskBits = goldenMask
    for (i in 0 until 32) {
        if ((goldenMaskBits and 1 != 0) && (seenBits and 1 == 0)) {
            missingFields += descriptor.getElementName(i)
        }
        seenBits = seenBits shr 1
        goldenMaskBits = goldenMaskBits shr 1
    }

    throw MissingFieldException(missingFields, descriptor.serialName)
}

@OptIn(ExperimentalSerializationApi::class)
@InternalSerializationApi
public fun throwMissingFieldException(seenArray: IntArray, goldenMaskArray: IntArray, descriptor: SerialDescriptor) {
    val missingFields = mutableListOf<String>()

    for (maskSlot in goldenMaskArray.indices) {
        var seen = seenArray[maskSlot]
        var goldenMask = goldenMaskArray[maskSlot]

        if ((seen and goldenMask) != goldenMask) {
            for (i in 0 until 32) {
                if ((goldenMask and 1 != 0) && (seen and 1 == 0)) {
                    missingFields += descriptor.getElementName(maskSlot * 32 + i)
                }
                seen = seen shr 1
                goldenMask = goldenMask shr 1
            }
        }
    }
    throw MissingFieldException(missingFields, descriptor.serialName)
}
