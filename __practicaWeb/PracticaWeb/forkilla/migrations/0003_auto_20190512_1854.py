# Generated by Django 2.1.4 on 2019-05-12 18:54

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('forkilla', '0002_auto_20190512_1708'),
    ]

    operations = [
        migrations.AlterField(
            model_name='restaurant',
            name='featured_photo',
            field=models.ImageField(default='static/forkilla/img/1.jpeg', upload_to=''),
        ),
    ]
