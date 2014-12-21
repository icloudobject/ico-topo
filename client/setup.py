try:
    from setuptools import setup
except ImportError:
    from distutils.core import setup

config = {
    'description': 'ICloudObject data center topology',
    'author': 'ICloudObject',
    'url': 'http://github.com/icloudobject/ico-topo',
    'download_url': 'http://github.com/icloudobject/ico-topo',
    'author_email': 'icloudobject@gmail.com',
    'version': '0.1',
    'install_requires': ['nose'],
    'packages': ['icotopo','icotopo.awsclient','icotopo.ec2','icotopo.s3','icotopo.yidbclient'],
    'scripts': [],
    'name': 'icotopo'
}

setup(**config)
