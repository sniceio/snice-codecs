package io.snice.codecs.codec.gtp.type;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class SelectionModeTypeTest {

    @Test
    public void testSelectionMode() {
        ensureSelectionMode(SelectionModeType.ofValue(0), 0);
        ensureSelectionMode(SelectionModeType.ofValue(1), 1);
        ensureSelectionMode(SelectionModeType.ofValue(2), 2);
        ensureSelectionMode(SelectionModeType.ofValue(3), 3);

        ensureSelectionMode(SelectionModeType.parse(SelectionModeType.ofValue(0).getBuffer()), 0);
        ensureSelectionMode(SelectionModeType.parse(SelectionModeType.ofValue(1).getBuffer()), 1);
        ensureSelectionMode(SelectionModeType.parse(SelectionModeType.ofValue(2).getBuffer()), 2);
        ensureSelectionMode(SelectionModeType.parse(SelectionModeType.ofValue(3).getBuffer()), 3);
    }

    private void ensureSelectionMode(final SelectionModeType sm, final int expectedMode) {
        assertThat(sm.getMode(), is(expectedMode));

    }


}