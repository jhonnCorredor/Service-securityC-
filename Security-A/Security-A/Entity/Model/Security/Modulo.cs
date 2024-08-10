﻿using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Entity.Model.Security
{
    public class Modulo
    {
        public int Id { get; set; }
        public string Name { get; set; }
        public Boolean State { get; set; }
        public string Description { get; set; }
        public int Position { get; set; }
        public DateTime Created_at { get; set; }
        public DateTime? Updated_at { get; set; } 
        public DateTime? Deleted_at { get; set; } 
    }
}
