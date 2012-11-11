package oxygenoffice.extensions.smart.diagram.organizationcharts.tablehierarchyorgchart;

import com.sun.star.awt.Point;
import com.sun.star.awt.Size;
import com.sun.star.drawing.XShape;
import oxygenoffice.extensions.smart.diagram.organizationcharts.OrganizationChart;
import oxygenoffice.extensions.smart.diagram.organizationcharts.OrganizationChartTree;
import oxygenoffice.extensions.smart.diagram.organizationcharts.OrganizationChartTreeItem;


class TableHierarchyOrgChartTreeItem extends OrganizationChartTreeItem{


    private static double[]     _maxPositions;
    private static double       _maxPos             = -1.0;

    private double              m_WidthUnit         = 1.0;
    private double              m_LastSiblingUnit   = 1.0;

    private static int          _shapeWidth         = 0;
    private static int          _shapeHeight        = 0;
    private static int          _groupPosX          = 0;
    private static int          _groupPosY          = 0;


    TableHierarchyOrgChartTreeItem(OrganizationChartTree diagramTree, OrganizationChartTreeItem dad, OrganizationChartTreeItem item) {
        super(diagramTree, dad, item);
    }
    
    public TableHierarchyOrgChartTreeItem(OrganizationChartTree diagramTree, XShape xShape, OrganizationChartTreeItem dad, short level, double num){
        super(diagramTree, xShape, dad);
        setLevel(level);
        setPos(num);
        m_WidthUnit         = 1.0;
        m_LastSiblingUnit   = 1.0;
    }

    public static void initStaticMembers(){
        _maxLevel = -1;
        _maxPos = -1.0;
        _maxPositions = new double[100];
        for(int i=0; i<_maxPositions.length; i++)
            _maxPositions[i] = -1.0;
    }

    @Override
    public void convertTreeItems(OrganizationChartTreeItem treeItem){
        if(treeItem.isFirstChild()){
            m_FirstChild = new TableHierarchyOrgChartTreeItem(getDiagramTree(), this, treeItem.getFirstChild());
            m_FirstChild.convertTreeItems(treeItem.getFirstChild());
        }
        if(treeItem.isFirstSibling()){
            m_FirstSibling = new TableHierarchyOrgChartTreeItem(getDiagramTree(), getDad(), treeItem.getFirstSibling());
            m_FirstSibling.convertTreeItems(treeItem.getFirstSibling());
        }
    }

    public double getWidthUnit(){
        return m_WidthUnit;
    }

    public void setWidthUnit(double unit){
        m_WidthUnit = unit;
    }

    public double getLastSiblingUnit(){
        return m_LastSiblingUnit;
    }

    public void setLastSiblingUnit(double unit){
        m_LastSiblingUnit = unit;
    }

    @Override
    public final void setPos(double pos){
        m_Pos = pos;
        if(m_Pos > _maxPositions[m_Level])
            _maxPositions[m_Level] = m_Pos;
        if(m_Pos > _maxPos)
            _maxPos = m_Pos;
        //XText xText = (XText) UnoRuntime.queryInterface(XText.class, m_xRectangleShape);
        //xText.setString(getWidthUnit() + " : " + (int)getLastSiblingUnit());
    }

    @Override
    public void initTreeItems(){
        XShape xFirstChildShape = getDiagramTree().getFirstChildShape(m_xRectangleShape);
        if(xFirstChildShape != null){
            short firstChildLevel = (short)(m_Level + 1);
            double firstChildPos = _maxPositions[firstChildLevel] + 1.0;
            m_FirstChild = new TableHierarchyOrgChartTreeItem(getDiagramTree(), xFirstChildShape, this, firstChildLevel , firstChildPos);
            m_FirstChild.initTreeItems();
        }

        XShape xFirstSiblingShape = getDiagramTree().getFirstSiblingShape(m_xRectangleShape, m_Dad);
        if(xFirstSiblingShape != null){
            short firstSiblingLevel = m_Level;
            double firstSiblingNum = m_Pos + 1.0;
            m_FirstSibling = new TableHierarchyOrgChartTreeItem(getDiagramTree(), xFirstSiblingShape, m_Dad, firstSiblingLevel , firstSiblingNum);
            m_FirstSibling.initTreeItems();
            setLastSiblingUnit(((TableHierarchyOrgChartTreeItem)m_FirstSibling).getLastSiblingUnit());
        }else{
            setLastSiblingUnit(getWidthUnit());
        }

        if(m_Dad != null && m_Dad.getFirstChild().equals(this)){
            if(isFirstSibling())
                ((TableHierarchyOrgChartTreeItem)m_Dad).setWidthUnit(_maxPositions[m_Level] - m_Pos + getLastSiblingUnit());
            else
                ((TableHierarchyOrgChartTreeItem)m_Dad).setWidthUnit(getWidthUnit());
            if(m_Pos > m_Dad.getPos())
                m_Dad.setPos(m_Pos);
            if(m_Pos < m_Dad.getPos())
                increasePosInBranch(m_Dad.getPos() - m_Pos);
        }
    
    }

    @Override
    public void setPositionsOfItems(){
        if(isFirstChild()){
            short firstChildLevel = (short)(m_Level + 1);
            double firstChildPos = _maxPositions[firstChildLevel] + 1.0;
            m_FirstChild.setLevel(firstChildLevel);
            m_FirstChild.setPos(firstChildPos);

            m_FirstChild.setPositionsOfItems();
        }

        if(isFirstSibling()){
            short firstSiblingLevel = m_Level;
            double firstSiblingNum = m_Pos + getWidthUnit();
            m_FirstSibling.setLevel(firstSiblingLevel);
            m_FirstSibling.setPos(firstSiblingNum);
            m_FirstSibling.setPositionsOfItems();
            setLastSiblingUnit(((TableHierarchyOrgChartTreeItem)m_FirstSibling).getLastSiblingUnit());
        }else{
            setLastSiblingUnit(getWidthUnit());
        }

        if(m_Dad != null && m_Dad.getFirstChild().equals(this)){
            if(isFirstSibling())
                ((TableHierarchyOrgChartTreeItem)m_Dad).setWidthUnit(_maxPositions[m_Level] - m_Pos + getLastSiblingUnit());
            else
                ((TableHierarchyOrgChartTreeItem)m_Dad).setWidthUnit(getWidthUnit());
            if(m_Pos > m_Dad.getPos())
                m_Dad.setPos(m_Pos);
            if(m_Pos < m_Dad.getPos())
                increasePosInBranch(m_Dad.getPos() - m_Pos);
        }
        //XText xText = (XText) UnoRuntime.queryInterface(XText.class, m_xRectangleShape);
        //xText.setString(getWidthUnit() + " : " + (int)getLastSiblingUnit());
    }

    // set _horUnit, _shapeWidth, , horSpace, _verUnit, _shapeHeight, verSpace, _groupPosX, _groupPosY
    @Override
    public void setMeasureProps(){
        int iHiddenElementNum = 0;
        if(getDiagramTree().getOrgChart().isHiddenRootElementProp())
            iHiddenElementNum = 1;
        
        int baseShapeWidth = getDiagramTree().getControlShapeSize().Width;
        int baseShapeHeight = getDiagramTree().getControlShapeSize().Height;

        _shapeWidth =  (int)(baseShapeWidth / (_maxPos + 1));
        _shapeHeight = baseShapeHeight / ((_maxLevel + 1) - iHiddenElementNum);

        _groupPosX = getDiagramTree().getControlShapePos().X;
        _groupPosY = getDiagramTree().getControlShapePos().Y;
    }

    @Override
    public void setPosOfRect(){
        int xCoord = _groupPosX + (int)(_shapeWidth * getPos());
        int yCoord = _groupPosY + _shapeHeight * getLevel();
        if(getDiagramTree().getOrgChart().isHiddenRootElementProp()){
            if(this.equals(getDiagramTree().getRootItem()))
                yCoord = _groupPosY - 10;
            else
                yCoord = _groupPosY + _shapeHeight * (getLevel() - 1);
        }
        setPosition(new Point(xCoord, yCoord));

        int width = (int)(_shapeWidth * m_WidthUnit - TableHierarchyOrgChart.GAP - getDiagramTree().getOrgChart().getShapesLineWidhtProp());
        int height = _shapeHeight - TableHierarchyOrgChart.GAP - getDiagramTree().getOrgChart().getShapesLineWidhtProp();

        if(getDiagramTree().getOrgChart().isShadowProp()){
            width -= OrganizationChart.SHADOW_DIST1;
            height -= OrganizationChart.SHADOW_DIST1;
        }      
        setSize(new Size( width, height));
    }

}
