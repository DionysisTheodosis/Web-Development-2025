package gr.aegean.icsd.autorepair.user;

import org.openpdf.text.pdf.PdfPageEventHelper;
import org.openpdf.text.*;
import org.openpdf.text.pdf.*;

class FooterEvent extends PdfPageEventHelper {
    private final Font footerFont;

    public FooterEvent(Font footerFont) {
        this.footerFont = footerFont;
    }

    @Override
    public void onEndPage(PdfWriter writer, Document document) {
        PdfContentByte cb = writer.getDirectContent();
        String text = String.format("Page %d", writer.getPageNumber());

        ColumnText.showTextAligned(cb, Element.ALIGN_RIGHT,
                new Phrase(text, footerFont),
                document.right(),
                document.bottom() - 20,
                0);
    }
}