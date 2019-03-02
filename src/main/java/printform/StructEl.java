package printform;

import org.apache.poi.xwpf.usermodel.XWPFTableCell;

import java.util.List;

class StructEl {
    String oehrName;
    String formName;
    byte visible;
    byte multiplicity;
    String type;
    List<String> validValues;
    StructEl parent;
    List<StructEl> childs;

    StructEl(List<XWPFTableCell> cells) {

    }
}
