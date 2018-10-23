import re


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

    def _add(self, val, node):
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
                node.hl = self._add(val, node.left)
                if node.hl - node.hr == 2:
                    node = self._turn_right(node)
        else:
            if node.right is None:
                new_node = tree_node(val)
                new_node.parent = node
                node.right = new_node
                node.hr += 1
            else:
                node.hr = self._add(val, node.right)
                if node.hr - node.hl == 2:
                    node = self._turn_left(node)
        return max(node.hr, node.hl) + 1

    def add(self, val):
        """Adds element val to tree if it's not already in tree"""
        if self._search(val, self._root) is not None:
            return False

        if self._root is None:
            self._root = tree_node(val)
        else:
            self._add(val, self._root)
        return True

    def _search_near_node(self, node, search_cond):
        """Algorithm to search near by value node in branch.
        Returns node.
        """
        if search_cond == 'b':
            if node.right is not None:
                return node
        else:
            if node.left is not None:
                return node
        return self._search_near_node(node.left, search_cond)

    def _remove(self, val, node):
        """Recursive function to remove element"""
        if val == node.val:
            if node.right is None and node.left is None:
                parent = node.parent
                node.parent = None
                if parent.left is node:
                    parent.left = None
                else:
                    parent.right = None
                return 0
            else:
                if node.hl > node.hr:
                    buf = self._search_near_node(node.left, 'b')
                    node.val = buf.val
                    node.hl = self._remove(node.val, node.left)
                    if node.hr - node.hl == 2:
                        node = self._turn_left(node)
                else:
                    buf = self._search_near_node(node.right, 's')
                    node.val = buf.val
                    node.hr = self._remove(node.val, node.right)
                    if node.hl - node.hr == 2:
                        node = self._turn_right(node)

        elif val < node.val:
            node.hl = self._remove(val, node.left)
            if node.hr - node.hl == 2:
                node = self._turn_left(node)
        else:
            node.hr = self._remove(val, node.right)
            if node.hl - node.hr == 2:
                node = self._turn_right(node)
        return max(node.hl, node.hr) + 1

    def remove(self, val):
        """Removes element from tree"""
        if self._search(val, self._root) is None:
            return False

        self._remove(val, self._root)
        return True

    def _search(self, val, node):
        """Recursive function for search"""
        if node is None:
            return None
        elif val == node.val:
            return node
        elif val <= node.val:
            return self._search(val, node.left)
        else:
            return self._search(val, node.right)

    def search(self, val):
        """Returns str with search path if val founded,
        otherwise returns str 'Not founded'
        """
        node = self._search(val, self._root)
        if node is None:
            return 'Not found'

        res = []
        node = node.parent
        while node is not None:
            res.append(node)
            node = node.parent
        res.reverse()

        res_str = ''
        for el in res:
            res_str += '{} -> '.format(el)
        return res_str + val

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
    tree = avl_tree(['b0', 'a2', 'c0', 'b1', 'd0', 'a1', 'b2'])
    print('Initial tree:')
    print(tree)
    print('\nCommands:\n \
show\n \
add [id]\n \
remove [id]\n \
search [id]\n \
exit')

    while True:
        com = input('\n> ')
        print('')
        args = com.split(' ')
        if len(args) > 2:
            print('One argument expected\n')
            continue
        elif len(args) == 2:
            if not re.fullmatch('[a-z][0-9]', args[1]):
                print('ID must be [a-z][0-9].')
                continue
            else:
                if args[0] == 'add':
                    if not tree.add(args[1]):
                        print('Element is already exist')
                    else:
                        print('Success.\n')
                        print(tree)
                elif args[0] == 'search':
                    print(tree.search(args[1]))
                elif args[0] == 'remove':
                    if not tree.remove(args[1]):
                        print('Element is not exist')
                    else:
                        print('Success.\n')
                        print(tree)
                else:
                    print('Try harder.')
        else:
            if args[0] == 'show':
                print(tree)
            elif args[0] == 'exit':
                break
            else:
                print('Try harder.')
