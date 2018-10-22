class tree_node:
    """Node for AVL tree"""

    def __init__(self, value):
        self.val = value
        self.parent = None
        self.left = None
        self.right = None
        self.hl = 0
        self.hr = 0

    def __str__(self):
        return str(self.val)


class avl_tree:
    """Realisation of AVL tree"""

    def __init__(self, list=[]):
        self._root = None
        for val in list:
            self.add(val)

    def _turn_right(self, node):
        """Provides Right and LeftRight rotations

        Returns node that will be on place of previos
        node
        """
        if node.left.hr > node.left.hl:
            buf = node.left
            node.left = buf.right
            node.left.parent = node
            buf.right = node.left.left
            node.left.left = buf
            buf.parent = node.left

            buf.hr = node.left.hl
            node.left.hl = max(buf.hl, buf.hr) + 1

        parent = node.parent
        buf = node.left

        node.left = buf.right
        buf.right = node
        node.parent = buf

        node.hl = buf.hr
        buf.hr = max(node.hl, node.hr) + 1

        if parent is None:
            self._root = buf
            buf.parent = None
        elif parent.left is node:
            parent.left = buf
            buf.parent = parent
        else:
            parent.right = buf
            buf.parent = parent
        return buf

    def _turn_left(self, node):
        """Provides Left and RightLeft rotations

        Returns node that will be on place of previos
        node
        """
        if node.right.hl > node.right.hr:
            buf = node.right
            node.right = buf.left
            node.right.parent = node
            buf.left = node.right.right
            node.right.right = buf
            buf.parent = node.right

            buf.hl = node.right.hr
            node.right.hr = max(buf.hl, buf.hr) + 1

        parent = node.parent
        buf = node.right

        node.right = buf.left
        buf.left = node
        node.parent = buf

        node.hr = buf.hl
        buf.hl = max(node.hl, node.hr) + 1

        if parent is None:
            self._root = buf
            buf.parent = None
        elif parent.left is node:
            parent.left = buf
            buf.parent = parent
        else:
            parent.right = buf
            buf.parent = parent
        return buf

    def _add_to_node(self, val, node):
        """Recursive function that search place to put element val,
        adds element after finding a place and provides rebalancing
        if necessary.

        Returns height of this branch
        """
        if val <= node.val:
            if node.left is None:
                new_node = tree_node(val)
                new_node.parent = node
                node.left = new_node
                node.hl += 1
            else:
                node.hl = self._add_to_node(val, node.left)
                if node.hl - node.hr == 2:
                    node = self._turn_right(node)
        else:
            if node.right is None:
                new_node = tree_node(val)
                new_node.parent = node
                node.right = new_node
                node.hr += 1
            else:
                node.hr = self._add_to_node(val, node.right)
                if node.hr - node.hl == 2:
                    node = self._turn_left(node)
        return max(node.hr, node.hl) + 1

    def add(self, val):
        """Adds element val to tree if it's not already in tree"""
        if self.search(val):
            return False

        if self._root is None:
            self._root = tree_node(val)
        else:
            self._add_to_node(val, self._root)
        return True

    def _search_near_node(self, val, node, cond):
        """Algorithm to search near by value node in branch.
        Returns node.
        """
        if node.right is not None:
            res = self._search_near_node(val, node.right, 'less')
        elif node.left is not None:
            res = self._search_near_node(val, node.left, 'greater')
        else:
            res = node

        if cond == 'less':
            if node.val < res.val:
                return node
            else:
                return res
        else:
            if node.val > res.val:
                return node
            else:
                return res

    def _remove_at_node(self, val, node):
        """Recursive function to remove element"""
        if val == node.val:
            if node.right is None and node.left is None:
                parent = node.parent
                node.parent = None
                if parent.left is node:
                    parent.hl -= 1
                    parent.left = None
                    if parent.hr - parent.hl == 2:
                        node = self._turn_left(parent)
                else:
                    parent.hr -= 1
                    parent.right = None
                    if parent.hl - parent.hr == 2:
                        node = self._turn_right(parent)
            else:
                if node.hl > node.hr:
                    buf = self._search_near_node(val, node.left, 'greater')
                else:
                    buf = self._search_near_node(val, node.right, 'less')
                node.val = buf.val
                self._remove_at_node(buf.val, buf)

        elif val < node.val:
            self._remove_at_node(val, node.left)
        else:
            self._remove_at_node(val, node.right)

    def remove(self, val):
        """Removes element from tree"""
        if not self.search(val):
            return False

        self._remove_at_node(val, self._root)
        return True

    def _compare_to_node(self, val, node):
        """Recursive function of search"""
        if node is None:
            return False
        elif val == node.val:
            return True
        elif val <= node.val:
            return self._compare_to_node(val, node.left)
        else:
            return self._compare_to_node(val, node.right)

    def search(self, val):
        """Returns True if element val founded, otherwise returns False"""
        return self._compare_to_node(val, self._root)

    def _make_str(self, node, tab):
        """Recursive function to collect readable representation of tree"""
        if node is None:
            return '{:{fill}<{tab}}None\n'.format('', fill='.', tab=tab)
        return '{:{fill}<{tab}}{}({}, {})\n'.format(
            '', node.val, node.hl, node.hr, fill='.', tab=tab) +\
            self._make_str(node.left, tab + 2) +\
            self._make_str(node.right, tab + 2)

    def __str__(self):
        return self._make_str(self._root, 0)


if __name__ == '__main__':
    tree = avl_tree(['b8', 'a8', 'c4', 'b4', 'd7', 'b2'])
    print('Initial tree:')
    print(tree)
    print('\nCommands:\n \
show\n \
add [element]\n \
remove [element]\n \
search [element]\n \
exit')

    while True:
        com = input('\n> ')
        print('')
        args = com.split(' ')
        if args[0] == 'show':
            print(tree)
        elif args[0] == 'add':
            if not tree.add(args[1]):
                print('Element already exist')
            else:
                print('Success.\n')
                print(tree)
        elif args[0] == 'search':
            print(tree.search(args[1]))
        elif args[0] == 'remove':
            if not tree.remove(args[1]):
                print('Element not exist')
            else:
                print('Success.\n')
                print(tree)
        elif args[0] == 'exit':
            break
        else:
            print('\nCommands:\n add [element]\n show\n exit')
