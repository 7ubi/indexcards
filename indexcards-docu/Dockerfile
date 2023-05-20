FROM python:3.9.16-alpine

WORKDIR /app

# set environment variables
ENV PYTHONDONTWRITEBYTECODE 1
ENV PYTHONUNBUFFERED 1

# install dependencies
RUN pip install --upgrade pip
COPY requirements.txt /app
RUN pip install -r requirements.txt

# copy project
COPY . .

CMD ["python", "-m", "mkdocs", "serve", "-a", "0.0.0.0:8005"]