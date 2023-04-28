package dev.webfx.extras.timelayout.gantt;

import dev.webfx.extras.timelayout.impl.TimeLayoutBase;
import dev.webfx.extras.timelayout.impl.TimeProjector;
import javafx.collections.ListChangeListener;

import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * @author Bruno Salmon
 */
public class GanttLayout<C, T extends Temporal> extends TimeLayoutBase<C, T> implements TimeProjector<T> {

    private Function<C, ?> childParentReader;
    private final Map<Object, ParentRow<C, T>> parentRows = new HashMap<>();
    private boolean tetrisPacking;
    private boolean parentRowsCacheCleaningRequired;
    private ParentRow<C, T> lastParentRow;
    private int rowsCountBeforeLastParentRow;

    public void setChildParentReader(Function<C, ?> childParentReader) {
        this.childParentReader = childParentReader;
    }

    public void setTetrisPacking(boolean tetrisPacking) {
        this.tetrisPacking = tetrisPacking;
    }

    @Override
    protected void onChildrenChanged(ListChangeListener.Change<? extends C> c) {
        parentRowsCacheCleaningRequired = true;
        super.onChildrenChanged(c);
    }

    @Override
    protected TimeProjector<T> getTimeProjector() {
        return this;
    }

    @Override
    public double timeToX(T time, boolean start, boolean exclusive, double layoutWidth) {
        T timeWindowStart = getTimeWindowStart();
        T timeWindowEnd = getTimeWindowEnd();
        if (timeWindowStart == null || timeWindowEnd == null)
            return 0;
        long totalDays = timeWindowStart.until(timeWindowEnd, ChronoUnit.DAYS) + 1;
        long daysToTime = timeWindowStart.until(time, ChronoUnit.DAYS);
        if (start && exclusive || !start && !exclusive)
            daysToTime++;
        return layoutWidth * daysToTime / totalDays;
    }

    @Override
    protected int computeChildColumnIndex(int childIndex, C child, T startTime, T endTime, double startX, double endX) {
        return 0;
    }

    @Override
    protected int computeChildRowIndex(int childIndex, C child, T startTime, T endTime, double startX, double endX) {
        if (childIndex == 0) { // => means that this is the first call of a new pass over the children
            if (parentRowsCacheCleaningRequired)
                cleanParentRowsCache();
            lastParentRow = null;
            rowsCountBeforeLastParentRow = 0;
        }
        Object parent = childParentReader == null ? null : childParentReader.apply(child);
        ParentRow<C, T> parentRow = getOrCreateParentRow(parent);
        if (parentRow != lastParentRow) {
            if (lastParentRow != null)
                rowsCountBeforeLastParentRow += lastParentRow.getRowsCount();
            lastParentRow = parentRow;
        }
        int parentRowIndex = parentRow.computeChildRowIndex(childIndex, child, startTime, endTime, startX, endX, getWidth(), tetrisPacking);
        return rowsCountBeforeLastParentRow + parentRowIndex;
    }

    private ParentRow<C, T> getOrCreateParentRow(Object parent) {
        ParentRow<C, T> parentRow = parentRows.get(parent);
        if (parentRow == null)
            parentRows.put(parent, parentRow = new ParentRow<>());
        return parentRow;
    }

    private void cleanParentRowsCache() {
        parentRows.values().forEach(parentRow -> parentRow.cleanCache(children));
        parentRowsCacheCleaningRequired = false;
    }

    // Note: getRowsCount() can be called when child positions are still invalid, so at this point packedRows is not
    // up-to-date.
    @Override
    public int getRowsCount() {
        if (parentRowsCacheCleaningRequired) // happens when children have just been modified, but their position is still invalid,
            return super.getRowsCount(); // so we call the default implementation to update these positions (this will
        // update packedRows in the process through the successive calls to computeChildRowIndex()).
        // Otherwise, packedRows is up-to-date when reaching this point,
        return parentRows.values().stream().mapToInt(ParentRow::getRowsCount).sum();
    }

}
