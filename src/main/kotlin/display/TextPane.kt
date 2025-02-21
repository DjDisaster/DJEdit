import java.awt.AlphaComposite
import java.awt.Graphics
import java.awt.Graphics2D
import javax.swing.JTextPane
import javax.swing.RepaintManager

class TextPane : JTextPane() {

    init {
        isOpaque = false
        RepaintManager.currentManager(this).setDoubleBufferingEnabled(false)
    }

    override fun paintComponent(g: Graphics) {
        val g2 = g as Graphics2D
        g2.composite = AlphaComposite.getInstance(AlphaComposite.CLEAR, 1.0f)
        g2.fillRect(0, 0, width, height)
        g2.composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f)
        super.paintComponent(g)
    }
}
