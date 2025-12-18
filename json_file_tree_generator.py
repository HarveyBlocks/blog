import os

class Node:
    def __init__(self, name, children):
        self.name = name
        self.children = children
    
    @staticmethod
    def dictionary(name, children):
        return Node(name, children)
    
    @staticmethod
    def file(name):
        return Node(name, None)
    
    def to_json(self):
        if self.children is None:
            return '{"name":"' + self.name + '","children":null}'
        else:
            children_json = ','.join(child.to_json() for child in self.children)
            return '{"name":"' + self.name + '","children":[' + children_json + ']}'

def create(file_path):
    if not os.path.exists(file_path):
        raise FileNotFoundError(f"file not found: {file_path}")
    
    file_name = os.path.basename(file_path)
    
    if not os.path.isdir(file_path):
        return Node.file(file_name)
    
    files = os.listdir(file_path)
    children = []
    for file in files:
        child_path = os.path.join(file_path, file)
        children.append(create(child_path))
    
    return Node.dictionary(file_name, children)

def main():
    try:
        root = create("./")
    except Exception as e:
        raise RuntimeError(e)
    
    # 过滤掉以点开头的文件/文件夹
    filtered_children = [child for child in root.children if not child.name.startswith(".")]
    
    # 转换为JSON数组字符串
    file_array = '[' + ','.join(child.to_json() for child in filtered_children) + ']'
    
    output_path = "./source_tree.json"
    
    try:
        with open(output_path, 'w', encoding='utf-8') as f:
            f.write(file_array)
    except Exception as e:
        raise RuntimeError(e)

if __name__ == "__main__":
    main()