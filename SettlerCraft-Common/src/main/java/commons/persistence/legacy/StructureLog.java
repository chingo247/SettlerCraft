
/*
 * The MIT License
 *
 * Copyright 2015 Chingo.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package commons.persistence.legacy;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 *
 * @author Chingo
 */
@Embeddable
@Deprecated
public class StructureLog implements Serializable {

    @Column(updatable = false)
    private Timestamp createdAt;
    private Timestamp completedAt;
    private Timestamp removedAt;
    private Boolean autoremoved = false;

    protected StructureLog() {
        this.createdAt = new Timestamp(new Date().getTime());
    }

    public void setRemovedAt(Date removedAt) {
        if (removedAt != null) {
            this.removedAt = new Timestamp(removedAt.getTime());
        } else {
            this.removedAt = null;
        }
    }

    public Timestamp getCompletedAt() {
        return completedAt;
    }

    public Timestamp getRemovedAt() {
        return removedAt;
    }
    
    


    public void setCompletedAt(Date completedAt) {
        if (completedAt != null) {
            this.completedAt = new Timestamp(completedAt.getTime());
        } else {
            this.completedAt = null;
        }
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setAutoremoved(Boolean autoremoved) {
        this.autoremoved = autoremoved;
    }

    public Boolean isAutoremoved() {
        return autoremoved;
    }

}
