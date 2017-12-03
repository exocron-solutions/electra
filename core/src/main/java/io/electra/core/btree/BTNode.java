/*
 * MIT License
 *
 * Copyright (c) 2017 Felix Klauke, JackWhite20
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package io.electra.core.btree;


/**
 * Class BTNode
 *
 * @author tnguyen
 */
public class BTNode<K extends Comparable, V> {
    public final static int MIN_DEGREE = 5;
    public final static int LOWER_BOUND_KEYNUM = MIN_DEGREE - 1;
    public final static int UPPER_BOUND_KEYNUM = (MIN_DEGREE * 2) - 1;

    protected boolean mIsLeaf;
    protected int mCurrentKeyNum;
    protected BTKeyValue<K, V> mKeys[];
    protected BTNode mChildren[];


    public BTNode() {
        mIsLeaf = true;
        mCurrentKeyNum = 0;
        mKeys = new BTKeyValue[UPPER_BOUND_KEYNUM];
        mChildren = new BTNode[UPPER_BOUND_KEYNUM + 1];
    }


    protected static BTNode getChildNodeAtIndex(BTNode btNode, int keyIdx, int nDirection) {
        if (btNode.mIsLeaf) {
            return null;
        }

        keyIdx += nDirection;
        if ((keyIdx < 0) || (keyIdx > btNode.mCurrentKeyNum)) {
            return null;
        }

        return btNode.mChildren[keyIdx];
    }


    protected static BTNode getLeftChildAtIndex(BTNode btNode, int keyIdx) {
        return getChildNodeAtIndex(btNode, keyIdx, 0);
    }


    protected static BTNode getRightChildAtIndex(BTNode btNode, int keyIdx) {
        return getChildNodeAtIndex(btNode, keyIdx, 1);
    }


    protected static BTNode getLeftSiblingAtIndex(BTNode parentNode, int keyIdx) {
        return getChildNodeAtIndex(parentNode, keyIdx, -1);
    }


    protected static BTNode getRightSiblingAtIndex(BTNode parentNode, int keyIdx) {
        return getChildNodeAtIndex(parentNode, keyIdx, 1);
    }
}
