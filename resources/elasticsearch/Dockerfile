# Dockerfile

FROM python:2.7

COPY . /migration_app

WORKDIR /migration_app

RUN pip install -r requirements.txt

ENTRYPOINT ['python']

CMD ["es_migration.py"]