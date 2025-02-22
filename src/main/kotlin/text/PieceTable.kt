package me.djdisaster.piecetable

// prob not actually needed ngl

class PieceTable(var original: String) {

    private var addBuffer = ""
    private val pieces = mutableListOf<Piece>()
    private val undoStack = mutableListOf<List<Piece>>()
    private val redoStack = mutableListOf<List<Piece>>()

    init {
        pieces.add(Piece(BufferType.ORIGINAL, 0, original.length))
    }

    fun insert(index: Int, text: String) {
        undoStack.add(pieces.toList())
        redoStack.clear()
        insertInternal(index, text)
    }

    private fun insertInternal(index: Int, text: String) {
        val addStart = addBuffer.length
        addBuffer += text
        val newPiece = Piece(BufferType.ADD, addStart, text.length)
        val (pieceIdx, charOffset) = findPiece(index)
        val oldPiece = pieces[pieceIdx]

        if (charOffset == 0) {
            pieces.add(pieceIdx, newPiece)
        } else {
            pieces[pieceIdx] = Piece(oldPiece.buffer, oldPiece.start, charOffset)
            pieces.add(pieceIdx + 1, newPiece)
            val remainingLength = oldPiece.length - charOffset
            if (remainingLength > 0) {
                pieces.add(pieceIdx + 2, Piece(oldPiece.buffer, oldPiece.start + charOffset, remainingLength))
            }
        }
    }

    fun delete(index: Int, length: Int) {
        if (length <= 0) return
        undoStack.add(pieces.toList())
        redoStack.clear()
        deleteInternal(index, length)
    }

    private fun deleteInternal(index: Int, length: Int) {
        val deleteEnd = index + length
        var currentIndex = 0
        val newPieces = mutableListOf<Piece>()

        for (piece in pieces) {
            val pieceStart = currentIndex
            val pieceEnd = currentIndex + piece.length
            if (pieceEnd <= index || pieceStart >= deleteEnd) {
                newPieces.add(piece)
            } else {
                if (pieceStart < index) {
                    val leftLength = index - pieceStart
                    newPieces.add(Piece(piece.buffer, piece.start, leftLength))
                }
                if (pieceEnd > deleteEnd) {
                    val rightLength = pieceEnd - deleteEnd
                    val rightStart = piece.start + (piece.length - rightLength)
                    newPieces.add(Piece(piece.buffer, rightStart, rightLength))
                }
            }
            currentIndex += piece.length
        }
        pieces.clear()
        pieces.addAll(newPieces)
    }

    fun replace(index: Int, length: Int, text: String) {
        undoStack.add(pieces.toList())
        redoStack.clear()
        deleteInternal(index, length)
        insertInternal(index, text)
    }

    fun undo() {
        if (undoStack.isNotEmpty()) {
            redoStack.add(pieces.toList())
            pieces.clear()
            pieces.addAll(undoStack.removeAt(undoStack.lastIndex))
        }
    }

    fun redo() {
        if (redoStack.isNotEmpty()) {
            undoStack.add(pieces.toList())
            pieces.clear()
            pieces.addAll(redoStack.removeAt(redoStack.lastIndex))
        }
    }

    fun find(text: String): List<Int> {
        val fullText = getText()
        val indices = mutableListOf<Int>()
        var pos = fullText.indexOf(text)
        while (pos != -1) {
            indices.add(pos)
            pos = fullText.indexOf(text, pos + 1)
        }
        return indices
    }

    fun copy(start: Int, length: Int): String {
        return getText().substring(start, start + length)
    }

    fun cut(start: Int, length: Int): String {
        val result = copy(start, length)
        delete(start, length)
        return result
    }

    fun paste(index: Int, text: String) {
        insert(index, text)
    }

    fun setText(newText: String) {
        undoStack.add(pieces.toList())
        redoStack.clear()
        original = newText
        addBuffer = ""
        pieces.clear()
        pieces.add(Piece(BufferType.ORIGINAL, 0, newText.length))
    }

    fun substring(start: Int, end: Int): String {
        return getText().substring(start, end)
    }

    fun length(): Int {
        return getText().length
    }

    fun getText(): String {
        val sb = StringBuilder()
        for (piece in pieces) {
            val buffer = if (piece.buffer == BufferType.ORIGINAL) original else addBuffer
            sb.append(buffer.substring(piece.start, piece.start + piece.length))
        }
        return sb.toString()
    }

    private fun findPiece(index: Int): Pair<Int, Int> {
        var total = 0
        for (i in pieces.indices) {
            val piece = pieces[i]
            if (total + piece.length >= index) {
                return Pair(i, index - total)
            }
            total += piece.length
        }
        return Pair(pieces.size - 1, 0)
    }
}

class Piece(
    var buffer: BufferType,
    var start: Int,
    var length: Int
)

enum class BufferType { ORIGINAL, ADD }
