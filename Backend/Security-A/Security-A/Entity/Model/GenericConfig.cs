using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Microsoft.EntityFrameworkCore.Metadata.Builders;
using Entity.Model.Security;
using Entity.Model.Parameter;

namespace Entity.Model
{
    internal class GenericConfig
    {
        public void ConfigureUser(EntityTypeBuilder<User> builder)
        {
            builder.HasIndex(i => i.Username).IsUnique();
            builder.HasIndex(i => i.PersonId).IsUnique();
        }
        public void ConfigurePerson(EntityTypeBuilder<Person> builder)
        {
            builder.HasIndex(i => i.Document).IsUnique();
            builder.HasIndex(i => i.Email).IsUnique();
            builder.HasIndex(i => i.Phone).IsUnique();
            builder.HasIndex(i => i.Document).IsUnique();
        }
        public void ConfigureModulo(EntityTypeBuilder<Modulo> builder)
        {
            builder.HasIndex(i => i.Name).IsUnique();
        }
        public void ConfigureDepartament(EntityTypeBuilder<Departament> builder)
        {
            builder.HasIndex(i => i.Name).IsUnique();
        }
        public void ConfigureCity(EntityTypeBuilder<City> builder)
        {
            builder.HasIndex(i =>i.Name).IsUnique();
        }
    }
}
